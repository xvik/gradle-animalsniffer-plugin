package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.api.Task
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.CompileTaskInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.TaskInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils

/**
 * Resolve kotlin compile tasks.
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2024
 */
@CompileStatic(TypeCheckingMode.SKIP)
class KotlinCompileTasksCollector {

    List<CompileTaskInfo> collect(Project project, Class kotlinPlugin, boolean collectClasspath) {
        Class<Task> taskCls = kotlinPlugin.classLoader
                .loadClass('org.jetbrains.kotlin.gradle.tasks.KotlinCompile') as Class<Task>

        List<CompileTaskInfo> res = []
        project.tasks.withType(taskCls).asList().each {
            Set<Task> deps = PrintUtils.resolveDependencies(project, it)
            res.add(new CompileTaskInfo(name: it.name,
                    type: taskCls,
                    sourceDirs: PrintUtils.inferSourceRoots(project.files(it.sources).asFileTree),
                    classes: it.outputs.files.asList(),
                    classpath: collectClasspath ? it.libraries.files : [],
                    dependsOn: deps ? deps.collect {
                        new TaskInfo(name: it.name, type: PrintUtils.taskClass(it.class))
                    } : []))
        }
        return res.sort()
    }
}
