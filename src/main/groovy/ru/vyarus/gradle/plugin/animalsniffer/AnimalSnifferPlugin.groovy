package ru.vyarus.gradle.plugin.animalsniffer

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.plugins.quality.internal.AbstractCodeQualityPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.util.GradleVersion
import ru.vyarus.gradle.plugin.animalsniffer.signature.AnimalSnifferSignatureExtension
import ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask

/**
 * AnimalSniffer plugin. Implemented the same way as gradle quality plugins (checkstyle, pmd, findbugs):
 * main configuration 'animalsniffer' used to configure custom tasks, registered for each source set.
 * <p>
 * Signatures are expected to be defined with 'signature' configuration. Multiple signatures may be set.
 * If no signatures defined - no check will be performed.
 * <p>
 * Reports configuration is performed with reports section. Only text report supported. All violations
 * are always printed to console.
 * <p>
 * To support signature configuration, plugin registered 'animalsnifferSignature' configuration.
 * If this configuration used then 'animalsnifferSignature' task will be registered to build signature file.
 *
 * @author Vyacheslav Rusakov
 * @since 13.12.2015
 */
@CompileStatic
class AnimalSnifferPlugin extends AbstractCodeQualityPlugin<AnimalSniffer> {

    private static final String MINIMAL_GRADLE = '2.14'
    private static final String SIGNATURE_CONF = 'signature'
    private static final String ANIMALSNIFFER_CONF = 'animalsniffer'
    private static final String ANIMALSNIFFER_SIG_CONF = 'animalsnifferSignature'

    private AnimalSnifferExtension extension

    @Override
    protected String getToolName() {
        return 'AnimalSniffer'
    }

    @Override
    protected Class<AnimalSniffer> getTaskType() {
        return AnimalSniffer
    }

    @Override
    protected void beforeApply() {
        // due to base class refactor from groovy to java in gradle 2.14
        // plugin can't be launched on prior gradle versions
        GradleVersion version = GradleVersion.current()
        if (version < GradleVersion.version(MINIMAL_GRADLE)) {
            throw new GradleException("Animalsniffer plugin requires gradle $MINIMAL_GRADLE or above, " +
                    "but your gradle version is: $version.version. Use plugin version 1.0.1.")
        }

        configureBuildTasks()
    }

    @Override
    @SuppressWarnings('BuilderMethodWithSideEffects')
    protected void createConfigurations() {
        super.createConfigurations()
        project.configurations.create(SIGNATURE_CONF).with {
            visible = false
            transitive = true
            description = 'AnimalSniffer signatures'
        }
    }

    @Override
    protected CodeQualityExtension createExtension() {
        extension = project.extensions.<AnimalSnifferExtension> create(ANIMALSNIFFER_CONF, AnimalSnifferExtension)
        extension.toolVersion = '1.15'
        return extension
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    protected void configureTaskDefaults(AnimalSniffer task, String baseName) {
        Configuration signatures = project.configurations[SIGNATURE_CONF]
        Configuration animalsnifferConfiguration = project.configurations[ANIMALSNIFFER_CONF]
        animalsnifferConfiguration.defaultDependencies { dependencies ->
            dependencies.add(this.project.dependencies
                    .create("org.codehaus.mojo:animal-sniffer-ant-tasks:${this.extension.toolVersion}"))
        }
        task.conventionMapping.with {
            animalsnifferSignatures = { signatures }
            animalsnifferClasspath = { animalsnifferConfiguration }
            ignoreFailures = { extension.ignoreFailures }
            annotation = { extension.annotation }
            incremental = { extension.incremental }
        }

        task.reports.all { report ->
            report.conventionMapping.with {
                enabled = { true }
                destination = { new File(extension.reportsDir, "${baseName}.${report.name}") }
            }
        }
        task.doFirst {
            if (extension.ignore) {
                task.ignoreClasses = extension.ignore
            }
        }
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    protected void configureForSourceSet(SourceSet sourceSet, AnimalSniffer task) {
        task.description = "Run AnimalSniffer checks for ${sourceSet.name} classes"
        task.setSource(sourceSet.output)
        task.setClassesDir(sourceSet.output.classesDir)
        task.dependsOn sourceSet.classesTaskName
        task.conventionMapping.with {
            classpath = { sourceSet.compileClasspath }
            sourcesDirs = { sourceSet.allJava }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void configureBuildTasks() {
        AnimalSnifferSignatureExtension extension = project.extensions
                .create(ANIMALSNIFFER_SIG_CONF, AnimalSnifferSignatureExtension)

        project.afterEvaluate {
            // register build signature task if files specified
            if (!extension.files.empty) {
                addBuildSignatureTask(extension)
            }

            applyBuildTasksDefaults()
        }
    }

    private void addBuildSignatureTask(AnimalSnifferSignatureExtension extension) {
        BuildSignatureTask task = project.tasks.create(ANIMALSNIFFER_SIG_CONF, BuildSignatureTask)
        extension.files.each { task.files(it) }
        extension.signatures.each { task.signatures(it) }
        task.include = extension.include
        task.exclude = extension.exclude
        // project name by default to be compatible with maven artifacts
        task.outputName(extension.outputName ?: project.name)
    }

    private void applyBuildTasksDefaults() {
        project.tasks.withType(BuildSignatureTask) { BuildSignatureTask task ->
            if (task.animalsnifferClasspath == null) {
                task.animalsnifferClasspath = project.configurations[ANIMALSNIFFER_CONF]
            }
            if (task.output == null) {
                task.outputName(task.name)
            }
        }
    }
}
