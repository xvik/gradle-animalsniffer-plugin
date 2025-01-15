package ru.vyarus.gradle.plugin.animalsniffer.debug

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import ru.vyarus.gradle.plugin.animalsniffer.debug.collector.*
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.*

import static ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils.*

/**
 * Android plugin provides sourceSets task to inspect registered android source sets. This task do almost
 * the same but for all source sets: java, android and multiplatform.
 * <p>
 * Shows:
 * <ul>
 *    <li>Registered plugins
 *    <li>Java and kotlin compile tasks (and all tasks with compile in name)
 *    <li>Source sets (java, android, kotlin)
 *    <li>Android variants
 *    <li>Kotlin targets
 * </ul>
 * <p>
 * Task is used for android projects investigation during android support addition into plugin. Plus, might be used
 * in debug purposes after plugin release.
 *
 * @author Vyacheslav Rusakov
 * @since 24.11.2024
 */
@CompileStatic
@DisableCachingByDefault
@SuppressWarnings(['Println', 'ConfusingMethodName', 'StaticMethodsBeforeInstanceMethods',
        'DuplicateNumberLiteral', 'DuplicateStringLiteral'])
class PrintAnimalsnifferSourceInfoTask extends DefaultTask {

    public static final String NAME = 'printAnimalsnifferSourceInfo'

    @Input
    boolean printPlugins = true

    @Input
    boolean printCompileTasks = true

    @Input
    boolean printSourceSets = true

    @Input
    boolean printAndroidVariants = true

    @Input
    boolean printKotlinTargets = true

    @Input
    boolean printClasspath = true

    private final File rootProjectDir
    private final Provider<DebugModel> modelProvider

    PrintAnimalsnifferSourceInfoTask() {
        rootProjectDir = project.rootProject.rootDir
        modelProvider = project.provider { createModel(project, printClasspath) }
    }

    @TaskAction
    void action() {
        DebugModel model = modelProvider.get()

        if (printPlugins) {
            println '\n\n== [Plugins] ==============================================================='
            printPlugins(model.plugins)
        }

        if (printCompileTasks) {
            println '\n\n== [Compile Tasks] ==============================================================='
            printCompileTasks(rootProjectDir, model.compileTasks)
        }

        if (printSourceSets) {
            println '\n\n== [SourceSets] ==============================================================='

            printSourceSets(rootProjectDir, model)
        }

        if (printKotlinTargets && model.kotlinTargets) {
            printTargets(rootProjectDir, model.kotlinTargets)
        }

        if (printAndroidVariants && model.androidVariants) {
            printVariants(rootProjectDir, model.androidVariants)
        }

        println '\n\\===========================================================================================\n'
    }

    static DebugModel createModel(Project project, boolean printClasspath) {
        DebugModel model = new DebugModel()
        model.plugins = new PluginsCollector().collect(project)

        CompileTasksModel compile = new CompileTasksModel()
        compile.allTasks = new CompileTasksStructureCollector().collect(project)
        compile.javaTasks = new JavaCompileTasksCollector().collect(project, printClasspath)
        if (model.plugins.kotlinPlugin) {
            compile.kotlinTasks = new KotlinCompileTasksCollector()
                    .collect(project, model.plugins.kotlinPlugin, printClasspath)
        }
        model.compileTasks = compile

        if (project.plugins.findPlugin('java-base')) {
            model.java = new JavaSourceSetsInfoCollector().collect(project, printClasspath)
        }
        if (project.plugins.findPlugin('org.jetbrains.kotlin.multiplatform')) {
            MultiplatformSourceSetsInfoCollector collector = new MultiplatformSourceSetsInfoCollector()
            model.kotlin = collector.collect(project)
            model.kotlinTargets = collector.collectTargets(project, printClasspath)
        }

        boolean isLibrary = project.plugins.findPlugin('com.android.library')
        if (project.plugins.findPlugin('com.android.application') || isLibrary) {
            model.androidLibrary = isLibrary
            model.android = new AndroidSourceSetsInfoCollector().collect(project, printClasspath)
            model.androidVariants = new AndroidVariantsInfoCollector().collect(project, printClasspath)
        }
        return model
    }

    private static void printPlugins(PluginsModel model) {
        Closure print = { PluginInfo plug ->
            return String.format("${buildPrefix(2)}%-40s %-25s %s",
                    plug.id ?: '<no id>', plug.name ?: '', plug.type.name)
        }
        println title(1,
                "Plugins of potential interest ------------------------------------------ (${model.interest.size()})")
        model.interest.each { println print(it) }

        println title(1,
                "Other plugins ----------------------------------------------------------- (${model.other.size()})")
        model.other.each { println print(it) }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private static void printCompileTasks(File rootProjectDir, CompileTasksModel model) {
        println title(1,
                "Tasks containing 'compile' in name ------------------------------------ (${model.allTasks.size()})")
        println()

        // as list (compile tasks only)
        model.allTasks.each {
            println String.format("${buildPrefix(2)}%-40s %s", it.name, it.type.name)
        }

        List<CompileTaskInfo> tree = new CompileTasksStructureCollector().collectTree(model.allTasks)
        println title(1,
                "Compile tasks tree -------------------------------------------------------- (${tree.size()} roots)")

        tree.each { task ->
            printCompileTaskLeaf(2, task, [])
        }

        println title(1, "Java compile tasks --------------------------------------------- (${model.javaTasks.size()})")
        model.javaTasks.each {
            printInOutClasspath(rootProjectDir, 2, "[${it.name}] -----",
                    it.sourceDirs,
                    it.classes,
                    it.classpath
            )
        }

        if (model.kotlinTasks) {
            println title(1,
                    "Kotlin compile tasks ------------------------------------------ (${model.kotlinTasks.size()})")

            model.kotlinTasks.each {
                printInOutClasspath(rootProjectDir, 2, "[${it.name}] -----",
                        it.sourceDirs,
                        it.classes,
                        it.classpath
                )
            }
        }
    }

    @SuppressWarnings(['UnnecessaryPackageReference', 'Instanceof'])
    private static void printCompileTaskLeaf(Integer level,
                                             ru.vyarus.gradle.plugin.animalsniffer.debug.model.TaskInfo task,
                                             List<String> antiDuplicate) {
        boolean compileTask = task instanceof CompileTaskInfo
        String msg = title(level, String.format(compileTask ? '%-40s %s' : '%-40s (%s)',
                compileTask ? "[$task.name]" : task.name, task.type.name))
        // on empty line for simple dependencies
        if (level != 2) {
            msg = msg.replace('\n', '')
        }
        println msg
        if (!antiDuplicate.contains(task.name)) {
            // means task was already completely printed - next time print only name
            antiDuplicate.add(task.name)
            if (task.dependsOn) {
                task.dependsOn.each { printCompileTaskLeaf(level + 1, it, antiDuplicate) }
            }
        }
    }

    private static void printSourceSets(File rootProjectDir, DebugModel model) {
        if (model.java) {
            printSourceSets(rootProjectDir, 1,
                    'Java Source Sets -------------------------------------------------------', model.java)
        }
        if (model.kotlin) {
            printSourceSets(rootProjectDir, 1,
                    'Kotlin Multiplatform Source Sets -------------------------------------------------------',
                    model.kotlin, model.kotlinTargetSourceSetsIndex)
            if (model.android) {
                printKotlinDifference(rootProjectDir, 1, 'Android sources NOT COVERED by kotlin source sets',
                        model.android, model.kotlin)
            }
        }
        if (model.android) {
            printSourceSets(rootProjectDir, 1, "Android ${model.androidLibrary ? 'library' : 'application'} " +
                    'Source Sets ------------------------------------------------------',
                    model.android, model.androidVariantsSourceSetsIndex)
            if (model.kotlin) {
                printKotlinDifference(rootProjectDir, 1, 'Kotlin sources NOT COVERED by android source sets',
                        model.kotlin, model.android)
            }
        }
    }

    private static void printKotlinDifference(File rootProjectDir, int shift, String msg,
                                              List<SourceSetInfo> koltin, List<SourceSetInfo> android) {
        Map<File, String> kotlinIdx = [:]
        koltin.each { set -> set.sourceDirs.each { file -> kotlinIdx[file] = set.name } }

        Map<File, String> androidIdx = [:]
        android.each { set -> set.sourceDirs.each { file -> androidIdx[file] = set.name } }

        List<File> notCoveredByKotlin = new ArrayList<>(kotlinIdx.keySet())
        notCoveredByKotlin.removeAll(androidIdx.keySet())

        if (!notCoveredByKotlin.empty) {
            println title(shift, msg)
            notCoveredByKotlin.each {
                println String.format(buildPrefix(shift + 1) + '%-70s (%s)',
                        it.canonicalPath.replace(getRootPath(rootProjectDir), ''), kotlinIdx[it])
            }
        }
    }

    private static void printVariants(File rootProjectDir, List<AndroidVariantInfo> info) {
        println title(
                "== [Android Variants] ========================================================== (${info.size()})")

        info.each {
            println title(1, "${it.name} ===== (compiled by ${it.compileTaskName} task)")
            printSourceSets(rootProjectDir, 2, 'Source sets', it.sourceSets)

            // sources rendered above (level 1 because no title and actual level would be 2)
            printInOutClasspath(rootProjectDir, 1, null, null, it.classes, it.classpath)
        }
    }

    private static void printTargets(File rootProjectDir, List<KotlinTargetInfo> targets) {
        println title(
                "== [Kotlin targets] ========================================================= (${targets.size()})")

        targets.each {
            println title(1, "$it.name ----- ($it.platform platform, ${it.compilations.size()} compilations)")

            it.compilations.each {
                println title(2, "$it.name (compiled by $it.compileTaskName task)")

                if (it.sourceSets) {
                    printSourceSets(rootProjectDir, 3, 'Source sets', it.sourceSets)
                }

                // sources rendered above
                printInOutClasspath(rootProjectDir, 2, null, null, it.classes, it.classpath)

                if (it.associatedCompilations) {
                    println title(3, "Associated compilations (${it.associatedCompilations.size()})")
                    it.associatedCompilations.each {
                        println buildPrefix(4) + it
                    }
                }
            }
        }
    }

    private static void printSourceSets(File rootProjectDir, int shift, String name, List<SourceSetInfo> info,
                                        Map<String, String> inclusiveIndex = null) {
        println title(shift, "$name (${info.size()})")

        info.each {
            printInOutClasspath(rootProjectDir, shift + 1,
                    "$it.name -----" + (inclusiveIndex?.get(it.name)
                            ? " (${inclusiveIndex.get(it.name)})" : ''),
                    it.sourceDirs, it.classes, it.classpath
            )
        }
    }

    @SuppressWarnings('ParameterCount')
    private static void printInOutClasspath(File rootProjectDir,
                                            int shift,
                                            String msg,
                                            Collection<File> sources,
                                            Collection<File> classes,
                                            Collection<File> classpath) {
        if (msg) {
            println title(shift, msg)
        }

        if (sources) {
            if (classes || classpath) {
                println title(shift + 1, 'Sources')
                println renderSources(shift + 2, sources, rootProjectDir)
            } else {
                // short notion for kotlin and android source sets
                println renderSources(shift + 1, sources, rootProjectDir)
            }
        }

        if (classes) {
            println title(shift + 1, 'Output')
            println renderClasses(shift + 2, classes, rootProjectDir)
        }

        if (classpath) {
            println title(shift + 1, 'Classpath')
            println renderClasspath(rootProjectDir, shift + 2, classpath)
        }
    }
}
