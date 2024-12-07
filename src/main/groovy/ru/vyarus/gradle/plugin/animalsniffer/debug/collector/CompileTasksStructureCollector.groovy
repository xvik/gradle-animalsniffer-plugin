package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import groovy.transform.CompileStatic
import org.gradle.api.Project
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.CompileTaskInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.TaskInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils

/**
 * Searches for all tasks with "compile" and "classes" in name and render a tree (to understand build tasks structure).
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2024
 */
@CompileStatic
class CompileTasksStructureCollector {

    List<CompileTaskInfo> collect(Project project) {
        List<CompileTaskInfo> res = []
        project.tasks.each { task ->
            if (task.name.containsIgnoreCase('compile')
                    || task.name.containsIgnoreCase('classes')) {
                Class cls = PrintUtils.taskClass(task.class)
                List<TaskInfo> deps = PrintUtils.resolveDependencies(project, task).collect {
                    // 1 level deps only
                    new TaskInfo(name: it.name, type: PrintUtils.taskClass(it.class))
                }
                // resolving all dependent tree to group compile tasks as much as possible
                res.add(new CompileTaskInfo(name: task.name,
                        type: cls,
                        dependsOn: deps.sort()))
            }
        }
        // group by task class and sort by name
        return res.sort()
    }

    List<CompileTaskInfo> collectTree(List<CompileTaskInfo> info) {
        List<CompileTaskInfo> res = new ArrayList<>(info)
        // index only compile tasks
        Map<String, CompileTaskInfo> index = [:]
        info.each { index[it.name] = it }

        info.each {
            processTasks(index, res, it)
        }

        // cleanup duplicate dependencies (when same ask is on the root and deeper levels)
        res.each { cleanupTaskDeps(it) }
        return res
    }

    private void processTasks(Map<String, CompileTaskInfo> index, List<CompileTaskInfo> res, TaskInfo task) {
        // aggregation: filling in compile tasks inside dependency tree (also removing them from root list)
        List<TaskInfo> dependsTree = []
        // replacing pure TaskInfo dependency with actual compile task info (where appropriate)
        task.dependsOn.each {
            CompileTaskInfo dep = index[it.name]
            dependsTree.add(dep ?: it)
            if (dep) {
                // not a root task
                res.remove(dep)
                // not checking subtree as this task would be handled later (all root compile tasks processed)
            } else {
                processTasks(index, res, it)
            }
        }
        task.dependsOn = dependsTree
    }

    // returns list of all deeper tasks
    private Set<String> cleanupTaskDeps(TaskInfo task) {
        // all task dependencies (full depth)
        Set<String> deps = []
        List<TaskInfo> res = new ArrayList<>(task.dependsOn)
        task.dependsOn.each {
            deps.add(it.name)
            Set<String> sub = cleanupTaskDeps(it)
            deps.addAll(sub)

            task.dependsOn.each {
                if (sub.contains(it.name)) {
                    res.remove(it)
                }
            }
        }
        task.dependsOn = res
        return deps
    }
}
