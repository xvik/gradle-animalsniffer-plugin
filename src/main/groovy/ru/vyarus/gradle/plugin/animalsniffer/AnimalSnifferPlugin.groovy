package ru.vyarus.gradle.plugin.animalsniffer

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovy.transform.TypeCheckingMode
import kotlin.jvm.functions.Function1
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.specs.NotSpec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetOutput
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.GradleVersion
import ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask
import ru.vyarus.gradle.plugin.animalsniffer.signature.AnimalSnifferSignatureExtension
import ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask
import ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
import ru.vyarus.gradle.plugin.animalsniffer.util.ContainFilesSpec
import ru.vyarus.gradle.plugin.animalsniffer.util.ExcludeFilePatternSpec

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

    public static final String SIGNATURE_CONF = 'signature'
    public static final String ANIMALSNIFFER_CACHE = 'animalsnifferCache'

    private static final String CHECK_SIGNATURE = 'animalsniffer'
    private static final String BUILD_SIGNATURE = 'animalsnifferSignature'

    private Project project
    private AnimalSnifferExtension extension
    private AnimalSnifferSignatureExtension buildExtension
    private boolean init

    @Override
    void apply(Project project) {
        this.project = project
        project.plugins.withType(JavaBasePlugin) {
            initialize()
            registerJavaCheckTasks()
        }
        project.plugins.withId('com.android.library') {
            initialize()
            registerAndroidCheckTasks()
        }
        project.plugins.withId('com.android.application') {
            initialize()
            registerAndroidCheckTasks()
        }
    }

    private void initialize() {
        if (!init) {
            project.plugins.apply(ReportingBasePlugin)

            checkGradleCompatibility()
            registerShortcuts()
            registerConfigurations()
            registerExtensions()
            registerBuildTasks()
        }
        init = true
    }

    private void checkGradleCompatibility() {
        // due to base internal api changes in gradle 5.0 plugin can't be launched on prior gradle versions
        GradleVersion version = GradleVersion.current()
        if (version < GradleVersion.version('7.0')) {
            throw new GradleException('Animalsniffer plugin requires gradle 7.0 or above, ' +
                    "but your gradle version is: $version.version. Use plugin version 1.7.1.")
        }
    }

    private void registerShortcuts() {
        ExtraPropertiesExtension props = project.extensions.extraProperties
        // to allow custom tasks declaration without package
        props.set(AnimalSniffer.simpleName, AnimalSniffer)
        props.set(BuildSignatureTask.simpleName, BuildSignatureTask)
        props.set(SignatureInfoTask.simpleName, SignatureInfoTask)
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
        extension = project.extensions.create(CHECK_SIGNATURE, AnimalSnifferExtension, project)
        extension.conventionMapping.with {
            reportsDir = { project.extensions.getByType(ReportingExtension).file(CHECK_SIGNATURE) }
            sourceSets = { project.sourceSets }
        }

        buildExtension = project.extensions.create(BUILD_SIGNATURE, AnimalSnifferSignatureExtension)
    }

    @SuppressWarnings(['Indentation', 'NestedBlockDepth'])
    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerJavaCheckTasks() {
        // create tasks for each source set
        project.sourceSets.all { SourceSet sourceSet ->
            String sourceSetName = sourceSet.name
            SourceSetOutput sourceSetOut = sourceSet.output
            TaskProvider<AnimalSniffer> checkTask = project.tasks
                    .<AnimalSniffer> register(sourceSet.getTaskName(CHECK_SIGNATURE, null),
                            AnimalSniffer) {
                        description = "Run AnimalSniffer checks for ${sourceSetName} classes"
                        // task operates on classes instead of sources
                        source = sourceSetOut
                        reports.all { report ->
                            report.required.convention(true)
                            report.outputLocation.convention(project.provider {
                                { -> new File(extension.reportsDir, "${sourceSetName}.${report.name}") } as RegularFile
                            })
                        }
                    }
            configureCheckTask(checkTask,
                    project.provider { project.files(sourceSet.allJava.srcDirs) },
                    sourceSet.getTaskName(ANIMALSNIFFER_CACHE, null),
                    sourceSet.classesTaskName,
                    sourceSet.compileClasspath)
        }

        // include required animalsniffer tasks in check lifecycle
        project.tasks.named('check').configure {
            dependsOn {
                extension.sourceSets*.getTaskName(CHECK_SIGNATURE, null)
            }
        }
    }

    @SuppressWarnings('GroovyAssignabilityCheck')
    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerAndroidCheckTasks() {
        Object androidComponents = project.androidComponents
        androidComponents.onVariants(androidComponents.selector().all()) { variant ->
            String sourceSetName = variant.name
            String capitalizedSourceSetName = sourceSetName.capitalize()
            String classesCollectorTaskName = sourceSetName + 'AnimalSnifferClassesCollector'
            TaskProvider<AndroidClassesCollector> classesCollector =
                    createAndroidClassesCollector(classesCollectorTaskName, variant)
            TaskProvider<AnimalSniffer> checkTask = project.tasks.<AnimalSniffer> register(
                    CHECK_SIGNATURE + capitalizedSourceSetName, AnimalSniffer) {
                description = "Run AnimalSniffer checks for ${sourceSetName} classes"
                // task operates on classes instead of sources
                source = classesCollector.flatMap { it.outputDirectory }
                reports.all { report ->
                    report.required.convention(true)
                    report.outputLocation.convention(project.provider {
                        { ->
                            new File(extension.reportsDir, "${sourceSetName}.${report.name}")
                        } as RegularFile
                    })
                }
            }

            configureCheckTask(checkTask,
                    project.provider { project.files(variant.sources.java.all, variant.sources.kotlin.all) },
                    ANIMALSNIFFER_CACHE + capitalizedSourceSetName,
                    classesCollectorTaskName,
                    variant.compileClasspath)
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    @SuppressWarnings('ClassForName')
    private TaskProvider<AndroidClassesCollector> createAndroidClassesCollector(String taskName, Object variant) {
        TaskProvider<AndroidClassesCollector> collectClasses = project.tasks.register(taskName, AndroidClassesCollector)
        // use variant class loader because plugin classpath did not "see" android deps
        Class scopedArtifactsScopeType = variant.class.classLoader
                .loadClass('com.android.build.api.variant.ScopedArtifacts$Scope')
        Class scopedArtifactTypeClasses = variant.class.classLoader
                .loadClass('com.android.build.api.artifact.ScopedArtifact$CLASSES')

        variant.artifacts.forScope(scopedArtifactsScopeType.PROJECT).use(collectClasses)
                .toGet(scopedArtifactTypeClasses.INSTANCE,
                        new Function1<AndroidClassesCollector, ListProperty<RegularFile>>() {
                            @Override
                            ListProperty<RegularFile> invoke(AndroidClassesCollector task) {
                                return task.jarFiles
                            }
                        },
                        new Function1<AndroidClassesCollector, ListProperty<Directory>>() {
                            @Override
                            ListProperty<Directory> invoke(AndroidClassesCollector task) {
                                return task.classesDirs
                            }
                        })
        return collectClasses
    }

    @SuppressWarnings(['Indentation', 'MethodSize', 'UnnecessaryGetter'])
    @CompileStatic(TypeCheckingMode.SKIP)
    private void configureCheckTask(TaskProvider<AnimalSniffer> checkTask,
                                    Provider<FileCollection> srcDirs,
                                    String signatureTaskName,
                                    String classesTaskName,
                                    FileCollection compileClasspath) {
        Configuration animalsnifferConfiguration = project.configurations[CHECK_SIGNATURE]

        // build special signature from provided signatures and all jars to be able to cache it
        // and perform much faster checks after the first run
        TaskProvider<BuildSignatureTask> signatureTask = project.tasks
                .<BuildSignatureTask> register(signatureTaskName, BuildSignatureTask) {
                    // this special task can be skipped if animalsniffer check supposed to be skipped
                    // note that task is still created because signatures could be registered dynamically
                    onlyIf { !extension.signatures.empty && extension.cache.enabled }

                    conventionMapping.with {
                        animalsnifferClasspath = { animalsnifferConfiguration }
                        signatures = { extension.signatures }
                        files = { excludeJars(getClasspathWithoutModules(compileClasspath)) }
                        exclude = { extension.cache.exclude as Set }
                        mergeSignatures = { extension.cache.mergeSignatures }
                        // debug for cache tasks controlled by check debug
                        debug = { extension.debug }
                    }
                }
        checkTask.configure {
            dependsOn(classesTaskName)
            // skip if no signatures configured or no sources to check
            onlyIf { !getAnimalsnifferSignatures().empty && getSource().size() > 0 }
            conventionMapping.with {
                classpath = {
                    extension.cache.enabled ?
                            getModulesFromClasspath(compileClasspath) : excludeJars(compileClasspath)
                }
                animalsnifferSignatures = {
                    extension.cache.enabled ? signatureTask.get().outputFiles : extension.signatures
                }
                animalsnifferClasspath = { animalsnifferConfiguration }
                // important to use provider here, because otherwise source set configuration changes would be ignored
                sourcesDirs = { srcDirs.get() }
                ignoreFailures = { extension.ignoreFailures }
                annotation = { extension.annotation }
                ignoreClasses = { extension.ignore }
                debug = { extension.debug }
            }
        }

        project.afterEvaluate {
            if (extension.cache.enabled) {
                // dependency must not be added earlier to avoid cache task appearance in log
                checkTask.configure {
                    dependsOn(signatureTask)
                }
            } else {
                // cache task should be always registered to simplify usage, but, by default, it is not enabled
                // cache task is not created at this point (gradle avoidance api)
                signatureTask.configure { enabled = false }
            }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerBuildTasks() {
        project.afterEvaluate {
            // register build signature task if files specified for signature creation
            if (buildExtension.configured) {
                project.tasks.register(BUILD_SIGNATURE, BuildSignatureTask) { task ->
                    buildExtension.files.each { task.files(it) }
                    buildExtension.signatures.each { task.signatures(it) }
                    task.include = buildExtension.include
                    task.exclude = buildExtension.exclude
                    // project name by default to be compatible with maven artifacts
                    task.outputName = buildExtension.outputName ?: project.name
                    // for project signature use hardcoded 'signature' folder instead of task name
                    task.outputDirectory = new File(project.buildDir, '/animalsniffer/signature/')
                    task.conventionMapping.debug = { buildExtension.debug }
                }
            }

            // defaults applied to all tasks (including manually created)
            project.tasks.withType(BuildSignatureTask).configureEach { task ->
                if (task.animalsnifferClasspath == null) {
                    task.animalsnifferClasspath = project.configurations[CHECK_SIGNATURE]
                }
                if (task.outputName == null) {
                    task.outputName = task.name
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
    private FileCollection getClasspathWithoutModules(FileCollection compileClasspath) {
        Set<File> excludeJars = moduleJars
        if (excludeJars.empty) {
            return compileClasspath
        }
        compileClasspath.filter new NotSpec<File>(new ContainFilesSpec(excludeJars))
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
    private FileCollection getModulesFromClasspath(FileCollection compileClasspath) {
        Set<File> includeJars = moduleJars
        if (includeJars.empty) {
            return null
        }
        compileClasspath.filter new ContainFilesSpec(includeJars)
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

    /**
     * Exclude some jars from the classpath. Required to support small 3rd party library signatures.
     *
     * @param classpath classpath file collection
     * @return filtered or original classpath if nothing configured to exclude
     */
    private FileCollection excludeJars(FileCollection classpath) {
        return extension.excludeJars ? classpath.filter(new ExcludeFilePatternSpec(extension.excludeJars))
                : classpath
    }
}
