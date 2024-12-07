package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.compile.JavaCompile
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.CompileTaskInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.TaskInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils

/**
 * Resolve {@link JavaCompile} tasks.
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2024
 */
@CompileStatic
class JavaCompileTasksCollector {

    List<CompileTaskInfo> collect(Project project, boolean collectClasspath) {
        List<CompileTaskInfo> res = []
        project.tasks.withType(JavaCompile).asList().each { task ->
            Set<Task> deps = PrintUtils.resolveDependencies(project, task)
            res.add(new CompileTaskInfo(name: task.name,
                    type: JavaCompile,
                    sourceDirs: PrintUtils.inferSourceRoots(project.files(task.source).asFileTree),
                    classes: task.outputs.files.asList() as Set,
                    classpath: collectClasspath ? task.classpath.files : [] as Set,
                    dependsOn: deps ? deps.collect {
                        new TaskInfo(name: it.name, type: PrintUtils.taskClass(it.class))
                    } : []))
        }
        return res.sort()
    }
}
