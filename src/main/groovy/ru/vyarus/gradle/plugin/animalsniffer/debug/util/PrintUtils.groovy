package ru.vyarus.gradle.plugin.animalsniffer.debug.util

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Action
import org.gradle.api.Buildable
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.api.internal.artifacts.transform.TransformNodeDependency
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileTreeInternal
import org.gradle.api.internal.tasks.WorkDependencyResolver
import org.gradle.api.internal.tasks.WorkNodeAction
import org.gradle.api.internal.tasks.compile.CompilationSourceDirs
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider

/**
 * Utilities for debug info print and resolution.
 *
 * @author Vyacheslav Rusakov
 * @since 28.11.2024
 */
@CompileStatic
@SuppressWarnings(['Println', 'PrintStackTrace', 'Instanceof'])
class PrintUtils {

    private static final WorkDependencyResolver<Task> IGNORE_ARTIFACT_TRANSFORM_RESOLVER
            = new WorkDependencyResolver<Task>() {
        @Override
        boolean resolve(Task task, Object node, Action<? super Task> resolveAction) {
            return node instanceof TransformNodeDependency || node instanceof WorkNodeAction
        }
    }
    private static final String NL = '\n'

    static Class taskClass(Class type) {
        return type.simpleName.endsWith('_Decorated') ? type.superclass : type
    }

    static Set<File> inferSourceRoots(FileTree sources) {
        // internal api!
        return CompilationSourceDirs.inferSourceRoots(sources as FileTreeInternal) as Set
    }

    @SuppressWarnings('CatchException')
    static Set<Task> resolveDependencies(Project project, Task task) {
        try {
            // see Task.getTaskDependencies().getDependencies(task)
            DependencyResolutionContext<Task> context = new DependencyResolutionContext<>(
                    [WorkDependencyResolver.TASK_AS_TASK, IGNORE_ARTIFACT_TRANSFORM_RESOLVER])
            return context.getDependencies(task, task.taskDependencies)
        } catch (Exception ex) {
            println "WARN: failed to resolve $task.name task dependencies. Use less accurate fallback"
            ex.printStackTrace()
            return resolveDependenciesFallback(project, task)
        }
    }

    // fallback could "see" far less dependencies (for complex cases, like android)
    static Set<Task> resolveDependenciesFallback(Project project, Task task) {
        Set<Task> deps = []
        println "$task.name $task.dependsOn"
        task.dependsOn.each {
            Set<Task> dep = resolveDependentTask(project, task, it)
            if (dep) {
                deps.addAll(dep)
            }
        }
        return deps
    }

    @SuppressWarnings('CatchException')
    static Set<File> resolve(Configuration conf, String context) {
        try {
            return conf.resolve()
        } catch (Exception ex) {
            println "WARN: failed to resolve $conf.name configuration $context. Ignoring"
            ex.printStackTrace()
            return [] as Set
        }
    }

    static String title(int shift = 0, String title) {
        return NL + buildPrefix(shift) + title
    }

    static String renderSources(int shift, Collection<File> sourceDirs, Project project) {
        String prefix = buildPrefix(shift)
        return sourceDirs.collect {
            String path = project.rootProject.relativePath(it)
            it.exists() ? "$prefix$path" : String.format("$prefix%-80s %s", path, it.exists() ? '' : 'NOT EXISTS')
        }
                .unique().sort().join(NL)
    }

    static String renderClasses(int shift, Collection<File> classDirs, Project project) {
        String prefix = buildPrefix(shift)
        return classDirs.collect { prefix + project.rootProject.relativePath(it) }
                .unique().sort().join(NL)
    }

    static String renderClasspath(Project project, int shift, Collection<File> files) {
        String prefix = buildPrefix(shift)
        files.collect {
            prefix + (it.canonicalPath.startsWith(project.rootDir.canonicalPath)
                    ? project.rootProject.relativePath(it) : it.name)
        }.sort().unique().join(NL)
    }

    static String buildPrefix(int shift) {
        String res = ''
        shift.times { res += '\t' }
        return res
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    @SuppressWarnings(['CouldBeSwitchStatement', 'BlockEndsWithBlankLine'])
    private static Set<Task> resolveDependentTask(Project project, Task task, Object depends) {
        Object dep = depends
        // unProvider
        if (dep instanceof Provider) {
            dep = (dep as Provider).orNull
            if (!dep) {
                return []
            }
        }

        if (dep instanceof Task) {
            return [dep]

        } else if (dep instanceof TaskProvider) {
            Task d = (dep as TaskProvider).orNull as Task
            return d ? [d] : []

        } else if (dep instanceof String) {
            return [project.tasks.findByName(dep as String)]

        } else if (dep instanceof Buildable) {
            return (dep as Buildable).buildDependencies.getDependencies(task)

        } else if (dep instanceof DefaultSourceDirectorySet) {
            // indirect dependency by source set (task output)
            TaskProvider<?> provider = (dep as DefaultSourceDirectorySet).compileTaskProvider
            Task d = provider ? provider.orNull : null
            return d ? [d] : []

        } else if (dep instanceof ConfigurableFileTree) {
            Set<Task> res = []
            (dep as ConfigurableFileTree).builtBy.each {
                res.addAll(resolveDependentTask(project, task, it))
            }
            return res

        }

        println "WARN: unknown dependency ${dep.class} ($dep) for task ${task.name}"
        return []
    }
}
