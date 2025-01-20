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
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.specs.NotSpec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.GradleVersion
import ru.vyarus.gradle.plugin.animalsniffer.debug.PrintAnimalsnifferSourceInfoTask
import ru.vyarus.gradle.plugin.animalsniffer.debug.PrintAnimalsnifferTasksTask
import ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask
import ru.vyarus.gradle.plugin.animalsniffer.signature.AnimalSnifferSignatureExtension
import ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask
import ru.vyarus.gradle.plugin.animalsniffer.support.reactor.AndroidComponentsReactor
import ru.vyarus.gradle.plugin.animalsniffer.support.reactor.MultiplatformTargetsReactor
import ru.vyarus.gradle.plugin.animalsniffer.support.task.AndroidTaskConfigurationProvider
import ru.vyarus.gradle.plugin.animalsniffer.support.task.AnimalsnifferTaskConfigurationProvider
import ru.vyarus.gradle.plugin.animalsniffer.support.task.JavaTaskConfigurationProvider
import ru.vyarus.gradle.plugin.animalsniffer.support.task.MultiplatformTaskConfigurationProvider
import ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
import ru.vyarus.gradle.plugin.animalsniffer.util.ContainFilesSpec
import ru.vyarus.gradle.plugin.animalsniffer.util.ExcludeFilePatternSpec
import ru.vyarus.gradle.plugin.animalsniffer.util.TargetType

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
    public static final String ANIMALSNIFFER_CACHE_START = 'cache'
    public static final String ANIMALSNIFFER_CACHE_END = 'Signatures'
    public static final String ANIMALSNIFFER_TASKS_GROUP = 'animalsniffer'

    public static final String CHECK_TASK = 'check'
    public static final String AS_CHECK_TASK_DESCR = 'Run AnimalSniffer checks'

    private static final String CHECK_SIGNATURE = 'animalsniffer'
    private static final String BUILD_SIGNATURE = 'animalsnifferSignature'

    private static final String PLUGIN_MULTIPLATFORM = 'org.jetbrains.kotlin.multiplatform'
    private static final String PLUGIN_ANDROID_LIB = 'com.android.library'
    private static final String PLUGIN_ANDROID_APP = 'com.android.application'

    private Project project
    private AnimalSnifferExtension extension
    private AnimalSnifferSignatureExtension buildExtension
    private boolean init

    @Override
    void apply(Project project) {
        this.project = project
        project.plugins.withType(JavaBasePlugin) {
            // ignore if multiplatform registered (in case of jvm().withJava() duplicate tasks would appear)
            if (!project.plugins.findPlugin(PLUGIN_MULTIPLATFORM)) {
                initialize()
                registerJavaCheckTasks()
            }
        }
        project.plugins.withId(PLUGIN_ANDROID_LIB) {
            initialize()
            registerAndroidCheckTasks()
        }
        project.plugins.withId(PLUGIN_ANDROID_APP) {
            initialize()
            registerAndroidCheckTasks()
        }
        project.plugins.withId(PLUGIN_MULTIPLATFORM) {
            initialize()
            registerMultiplatformCheckTasks()
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
            registerDebugTasks()
        }
        init = true
    }

    private void checkGradleCompatibility() {
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
        props.set(TargetType.simpleName, TargetType)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerConfigurations() {
        project.configurations.create(CHECK_SIGNATURE).with {
            visible = false
            transitive = true
            description = 'The AnimalSniffer libraries to be used for this project.'

            defaultDependencies { dependencies ->
                dependencies.add(this.project.dependencies
                        .create("org.codehaus.mojo:animal-sniffer:${this.extension.toolVersion}"))
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
            registerCheckTask(new JavaTaskConfigurationProvider(project.objects, project.providers, sourceSet))
        }
    }

    @SuppressWarnings('GroovyAssignabilityCheck')
    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerAndroidCheckTasks() {
        new AndroidComponentsReactor(project).onTarget { Object component ->
            TaskProvider<AndroidClassesCollector> classesCollector =
                    createAndroidClassesCollector(
                            AndroidClassesCollector.computeTaskName(component.name),
                            component)

            AndroidTaskConfigurationProvider provider = new AndroidTaskConfigurationProvider(
                    project.objects, project.providers, component, classesCollector)
            registerCheckTask(provider)

            // can't print android info until some tasks execution
            project.tasks.withType(PrintAnimalsnifferTasksTask).configureEach {
                it.dependsOn(provider.compileTaskName)
            }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    @SuppressWarnings(['ClassForName', 'ClosureAsLastMethodParameter'])
    private TaskProvider<AndroidClassesCollector> createAndroidClassesCollector(String taskName, Object variant) {
        TaskProvider<AndroidClassesCollector> collectClasses = project.tasks.register(taskName, AndroidClassesCollector)
        // use variant class loader because plugin classpath did not "see" android deps
        Class scopedArtifactsScopeType = variant.class.classLoader
                .loadClass('com.android.build.api.variant.ScopedArtifacts$Scope')
        Class scopedArtifactTypeClasses = variant.class.classLoader
                .loadClass('com.android.build.api.artifact.ScopedArtifact$CLASSES')

        variant.artifacts.forScope(scopedArtifactsScopeType.PROJECT).use(collectClasses)
                .toGet(scopedArtifactTypeClasses.INSTANCE,
                        { it.jarFiles },
                        { it.classesDirs })
        return collectClasses
    }

    @SuppressWarnings(['GroovyAssignabilityCheck', 'NestedBlockDepth'])
    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerMultiplatformCheckTasks() {
        boolean androidPlugin = project.plugins.findPlugin(PLUGIN_ANDROID_LIB)
                || project.plugins.findPlugin(PLUGIN_ANDROID_APP)

        new MultiplatformTargetsReactor(project).onTarget { target ->
            target.compilations.all { compilation ->
                if (target.targetName == 'metadata') {
                    // skipping metadata target because it would be included in all other targets
                    return
                }

                // when android plugin registered - avoid creating tasks for android platform (duplication)
                if (!androidPlugin || target.targetName != 'android') {
                    MultiplatformTaskConfigurationProvider provider = new MultiplatformTaskConfigurationProvider(
                            project.objects, project.providers, compilation)
                    registerCheckTask(provider)
                } else {
                    project.tasks.named(AndroidClassesCollector.computeTaskName(compilation.name)).configure {
                        // add missed kotlin sources to existing animalsniffer (android) task
                        (it as AndroidClassesCollector).multiplatformSourceDirs.addAll(project.provider {
                            project.files(
                                    compilation.allKotlinSourceSets.collect { it.kotlin.sourceDirectories }).files
                        })
                    }
                }
            }
        }
    }

    @SuppressWarnings('UnnecessaryObjectReferences')
    private TaskProvider<AnimalSniffer> registerCheckTask(AnimalsnifferTaskConfigurationProvider config) {
        String taskName = CHECK_SIGNATURE + config.targetName.capitalize()
        // registration action would be evaluated lazily!
        TaskProvider<AnimalSniffer> checkTask = project.tasks.<AnimalSniffer> register(taskName, AnimalSniffer) {
            it.targetType = config.type
            it.targetName = config.targetName
            it.group = ANIMALSNIFFER_TASKS_GROUP
            it.description = AS_CHECK_TASK_DESCR + ' ' + config.description
            it.dependsOn config.compileTaskName
            // task operates on classes instead of sources
            it.source = config.classes
            it.sourcesDirs = config.sourceDirs
            it.reports.configureEach { report ->
                report.required.convention(true)
                report.outputLocation.convention(project.provider {
                    { -> new File(extension.reportsDir, "${config.targetName}.${report.name}") } as RegularFile
                })
            }
            it.ignoreFailures = extension.ignoreFailures
            it.annotation = extension.annotation
            it.ignoreClasses = extension.ignore
            it.debug = extension.debug
        }
        configureCheckTask(checkTask, config)
    }

    @SuppressWarnings(['Indentation', 'MethodSize', 'UnnecessaryGetter'])
    @CompileStatic(TypeCheckingMode.SKIP)
    private TaskProvider<AnimalSniffer> configureCheckTask(TaskProvider<AnimalSniffer> checkTask,
                                                           AnimalsnifferTaskConfigurationProvider config) {
        Configuration animalsnifferConfiguration = project.configurations[CHECK_SIGNATURE]

        // build special signature from provided signatures and all jars to be able to cache it
        // and perform much faster checks after the first run
        TaskProvider<BuildSignatureTask> signatureTask = project.tasks
                .<BuildSignatureTask> register(
                        "$ANIMALSNIFFER_CACHE_START${checkTask.name.capitalize()}$ANIMALSNIFFER_CACHE_END",
                        BuildSignatureTask) {
                    it.description = "Signatures cache for ${checkTask.name} task"
                    // signature file would be named the same as check task name
                    it.outputName = checkTask.name + 'Cache'
                    // this special task can be skipped if animalsniffer check supposed to be skipped
                    // note that task is still created because signatures could be registered dynamically
                    onlyIf { !extension.signatures.empty && extension.cache.enabled }

                    conventionMapping.with {
                        animalsnifferClasspath = { animalsnifferConfiguration }
                        signatures = { extension.signatures }
                        files = { excludeJars(getClasspathWithoutModules(config.compileClasspath)) }
                        exclude = { extension.cache.exclude as Set }
                        mergeSignatures = { extension.cache.mergeSignatures }
                        // debug for cache tasks controlled by check debug
                        debug = { extension.debug }
                    }
                }
        checkTask.configure {
            // skip if no sources to check
            onlyIf { it.getSource().size() > 0 }
            conventionMapping.with {
                classpath = {
                    extension.cache.enabled ?
                            getModulesFromClasspath(config.compileClasspath) : excludeJars(config.compileClasspath)
                }
                animalsnifferSignatures = {
                    extension.cache.enabled ? signatureTask.get().outputFiles : extension.signatures
                }
                animalsnifferClasspath = { animalsnifferConfiguration }
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

            assignToDefaultBuildIfRequired(checkTask, config)
        }

        return checkTask
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void registerBuildTasks() {
        project.afterEvaluate {
            // register build signature task if files specified for signature creation
            if (buildExtension.configured) {
                project.tasks.register(BUILD_SIGNATURE, BuildSignatureTask) { task ->
                    task.description = 'Build animalsniffer signature'
                    buildExtension.files.each { task.files(it) }
                    buildExtension.signatures.each { task.signatures(it) }
                    task.include = buildExtension.include
                    task.exclude = buildExtension.exclude
                    // project name by default to be compatible with maven artifacts
                    task.outputName = buildExtension.outputName ?: project.name
                    // for project signature use hardcoded 'signature' folder instead of task name
                    task.outputDirectory = new File(
                            project.layout.buildDirectory.asFile.get(), '/animalsniffer/signature/')
                    task.conventionMapping.debug = { buildExtension.debug }
                }
            }

            // defaults applied to all tasks (including manually created)
            project.tasks.withType(BuildSignatureTask).configureEach { task ->
                task.group = ANIMALSNIFFER_TASKS_GROUP
                if (task.animalsnifferClasspath == null) {
                    task.animalsnifferClasspath = project.configurations[CHECK_SIGNATURE]
                }
                if (task.outputName == null) {
                    task.outputName = task.name
                }
            }
        }
    }

    private void registerDebugTasks() {
        // print all registered animalsniffer tasks
        project.tasks.register(PrintAnimalsnifferTasksTask.NAME, PrintAnimalsnifferTasksTask) {
            it.group = ANIMALSNIFFER_TASKS_GROUP
            it.description = 'List animalsniffer tasks configurations'
        }
        // prints all available info (including registered plugins, source sets, compile tasks, etc)
        project.tasks.register(PrintAnimalsnifferSourceInfoTask.NAME, PrintAnimalsnifferSourceInfoTask) {
            it.group = ANIMALSNIFFER_TASKS_GROUP
            it.description = 'Show plugins, compile tasks and declared source sets ' +
                    '(for checking animalsniffer sources correctness)'
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

    private void assignToDefaultBuildIfRequired(TaskProvider<AnimalSniffer> task,
                                                AnimalsnifferTaskConfigurationProvider config) {
        if (!extension.checkTestSources && config.targetName.containsIgnoreCase('test')) {
            // skip test sources
            return
        }

        Set<String> targets = extension.defaultTargets
        boolean assign
        // by default, targets are null - use default behavior, but user could set to empty to disable all
        if (targets != null) {
            // match configured names
            assign = targets.find { config.targetName.equalsIgnoreCase(it) }
        } else {
            // all tasks are default
            // for java tasks, consult legacy sourceSets configuration
            assign = config.type != TargetType.SourceSet
                    || extension.sourceSets.find { it.name == config.targetName }
        }

        if (assign) {
            // include required animalsniffer tasks in check lifecycle
            project.tasks.named(CHECK_TASK).configure {
                it.dependsOn(task)
            }
        }
    }
}
