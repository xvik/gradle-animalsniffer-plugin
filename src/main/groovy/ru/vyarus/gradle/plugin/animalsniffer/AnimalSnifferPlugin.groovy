package ru.vyarus.gradle.plugin.animalsniffer

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovy.transform.TypeCheckingMode
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.specs.NotSpec
import org.gradle.api.tasks.SourceSet
import org.gradle.util.GradleVersion
import ru.vyarus.gradle.plugin.animalsniffer.signature.AnimalSnifferSignatureExtension
import ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask
import ru.vyarus.gradle.plugin.animalsniffer.util.ContainFilesSpec

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
            registerCheckTasks()
            registerBuildTasks()
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
    private void registerCheckTasks() {
        // create tasks for each source set
        project.sourceSets.all { SourceSet sourceSet ->
            AnimalSniffer task = project.tasks
                    .create(sourceSet.getTaskName(CHECK_SIGNATURE, null), AnimalSniffer)

            task.description = "Run AnimalSniffer checks for ${sourceSet.name} classes"
            // task operates on classes instead of sources
            task.setSource(sourceSet.output)
            configureCheckTask(task, sourceSet)
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
    private void configureCheckTask(AnimalSniffer task, SourceSet sourceSet) {
        Configuration configuredSignatures = project.configurations[SIGNATURE_CONF]
        Configuration animalsnifferConfiguration = project.configurations[CHECK_SIGNATURE]

        // build special signature from provided signatures and all jars to be able to cache it
        // and perform much faster checks after the first run
        BuildSignatureTask signatureTask = project.tasks
                .create(sourceSet.getTaskName('animalsnifferResources', null), BuildSignatureTask) {

            // this special task can be skipped if animalsniffer check supposed to be skipped
            // note that task is still created because signatures could be registered dynamically
            onlyIf { !configuredSignatures.empty && extension.useResourcesTask }

            conventionMapping.with {
                animalsnifferClasspath = { animalsnifferConfiguration }
                signatures = { configuredSignatures }
                files = { getClasspathWithoutModules(sourceSet) }
                exclude = { extension.resourcesExclude as Set }
            }
        }

        task.dependsOn(sourceSet.classesTaskName)
        task.conventionMapping.with {
            classpath = {
                extension.useResourcesTask ?
                        getModulesFromClasspath(sourceSet) : sourceSet.compileClasspath
            }
            animalsnifferSignatures = {
                extension.useResourcesTask ? signatureTask.outputs.files : configuredSignatures
            }
            animalsnifferClasspath = { animalsnifferConfiguration }
            sourcesDirs = { sourceSet.allJava }
            ignoreFailures = { extension.ignoreFailures }
            annotation = { extension.annotation }
            ignoreClasses = { extension.ignore }
        }

        project.afterEvaluate {
            if (extension.useResourcesTask) {
                // dependency must not be added earlier to avoid resources task appearance in log
                task.dependsOn(signatureTask)
            } else {
                // resource task should be always registered to simplify usage, but, by default, it is not enabled and
                // it's better to remove it to avoid confusion (task will not appear in the tasks list)
                project.tasks.remove(signatureTask)
            }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerBuildTasks() {
        project.afterEvaluate {
            // register build signature task if files specified for signature creation
            if (buildExtension.configured) {
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

    /**
     * In multi-module projects there is high probability that modules could change, so its safer to exclude them
     * from project specific signature to rebuild it less often (and speed up overall build time).
     *
     * @param sourceSet source set
     * @return source set classpath with excluded other module jars
     */
    @Memoized
    @CompileStatic(TypeCheckingMode.SKIP)
    private FileCollection getClasspathWithoutModules(SourceSet sourceSet) {
        Set<File> excludeJars = moduleJars
        if (excludeJars.empty) {
            return sourceSet.compileClasspath
        }
        sourceSet.compileClasspath.filter new NotSpec<File>(new ContainFilesSpec(excludeJars))
    }

    /**
     * As other module dependencies removed from project specific signature, they must be added to check task
     * classpath.
     *
     * @param sourceSet source set
     * @return file collection containing only module jars or null if no dependency on other modules
     */
    @Memoized
    @CompileStatic(TypeCheckingMode.SKIP)
    private FileCollection getModulesFromClasspath(SourceSet sourceSet) {
        Set<File> includeJars = moduleJars
        if (includeJars.empty) {
            return null
        }
        sourceSet.compileClasspath.filter new ContainFilesSpec(includeJars)
    }

    /**
     * @return list of all project modules jars as a set of patterns
     */
    @Memoized
    private Set<File> getModuleJars() {
        Set<File> patterns = []
        project.rootProject.allprojects.each {
            it.configurations.findByName(Dependency.DEFAULT_CONFIGURATION)?.allArtifacts?.each {
                patterns << it.file
            }
        }
        patterns
    }
}
