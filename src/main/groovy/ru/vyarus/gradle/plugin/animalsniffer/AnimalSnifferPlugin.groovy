package ru.vyarus.gradle.plugin.animalsniffer

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.ReportingExtension
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
class AnimalSnifferPlugin implements Plugin<Project> {

    private static final String SIGNATURE_CONF = 'signature'
    private static final String CHECK_SIGNATURE = 'animalsniffer'
    private static final String BUILD_SIGNATURE = 'animalsnifferSignature'

    private Project project
    private AnimalSnifferExtension extension
    private AnimalSnifferSignatureExtension buildExtension

    @Override
    void apply(Project project) {
        // activated only when java plugin is enabled
        project.plugins.withType(JavaPlugin) {
            this.project = project
            project.plugins.apply(ReportingBasePlugin)

            checkGradleCompatibility()
            registerExtensions()
            registerConfigurations()
            configureCheckTasks()
            configureBuildTasks()
        }
    }

    private void checkGradleCompatibility() {
        // due to base class refactor from groovy to java in gradle 2.14
        // plugin can't be launched on prior gradle versions
        GradleVersion version = GradleVersion.current()
        if (version < GradleVersion.version('2.14')) {
            throw new GradleException('Animalsniffer plugin requires gradle 2.14 or above, ' +
                    "but your gradle version is: $version.version. Use plugin version 1.0.1.")
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerConfigurations() {
        project.configurations.create(CHECK_SIGNATURE).with {
            visible = false
            transitive = true
            description = 'The AnimalSniffer libraries to be used for this project.'

            defaultDependencies { dependencies ->
                dependencies.add(this.project.dependencies
                        .create("org.codehaus.mojo:animal-sniffer-ant-tasks:${this.extension.toolVersion}"))
            }
        }

        project.configurations.create(SIGNATURE_CONF).with {
            visible = false
            transitive = true
            description = 'AnimalSniffer signatures'
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerExtensions() {
        extension = project.extensions.create(CHECK_SIGNATURE, AnimalSnifferExtension)
        extension.conventionMapping.with {
            reportsDir = { project.extensions.getByType(ReportingExtension).file(CHECK_SIGNATURE) }
            sourceSets = { project.sourceSets }
        }

        buildExtension = project.extensions.create(BUILD_SIGNATURE, AnimalSnifferSignatureExtension)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void configureCheckTasks() {
        Configuration signatures = project.configurations[SIGNATURE_CONF]
        Configuration animalsnifferConfiguration = project.configurations[CHECK_SIGNATURE]

        // create tasks for each source set
        project.sourceSets.all { SourceSet sourceSet ->
            AnimalSniffer task = project.tasks
                    .create(sourceSet.getTaskName(CHECK_SIGNATURE, null), AnimalSniffer)

            task.description = "Run AnimalSniffer checks for ${sourceSet.name} classes"
            // task operates on classes instead of sources
            task.setSource(sourceSet.output)
            task.dependsOn sourceSet.classesTaskName
            task.conventionMapping.with {
                classpath = { sourceSet.compileClasspath }
                sourcesDirs = { sourceSet.allJava }
                animalsnifferSignatures = { signatures }
                animalsnifferClasspath = { animalsnifferConfiguration }
                ignoreFailures = { extension.ignoreFailures }
                annotation = { extension.annotation }
                ignoreClasses = { extension.ignore }
            }
            task.reports.all { report ->
                report.conventionMapping.with {
                    enabled = { true }
                    destination = { new File(extension.reportsDir, "${sourceSet.name}.${report.name}") }
                }
            }
        }

        // include required animalsniffer tasks in check lifecycle
        project.tasks.check.dependsOn {
            extension.sourceSets*.getTaskName(CHECK_SIGNATURE, null)
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void configureBuildTasks() {
        project.afterEvaluate {
            // register build signature task if files specified for signature creation
            if (!buildExtension.files.empty) {
                BuildSignatureTask task = project.tasks.create(BUILD_SIGNATURE, BuildSignatureTask)
                buildExtension.files.each { task.files(it) }
                buildExtension.signatures.each { task.signatures(it) }
                task.include = buildExtension.include
                task.exclude = buildExtension.exclude
                // project name by default to be compatible with maven artifacts
                task.outputName(buildExtension.outputName ?: project.name)
            }

            // defaults applied to all tasks (including manually created)
            project.tasks.withType(BuildSignatureTask) { BuildSignatureTask task ->
                if (task.animalsnifferClasspath == null) {
                    task.animalsnifferClasspath = project.configurations[CHECK_SIGNATURE]
                }
                if (task.output == null) {
                    task.outputName(task.name)
                }
            }
        }
    }
}
