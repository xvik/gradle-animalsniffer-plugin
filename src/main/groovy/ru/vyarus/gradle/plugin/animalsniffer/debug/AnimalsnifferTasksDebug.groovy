package ru.vyarus.gradle.plugin.animalsniffer.debug

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import ru.vyarus.gradle.plugin.animalsniffer.AnimalSniffer
import ru.vyarus.gradle.plugin.animalsniffer.AnimalSnifferExtension
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils
import ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask

/**
 * Prints registered animalsniffer tasks.
 *
 * @author Vyacheslav Rusakov
 * @since 13.12.2024
 */
@CompileStatic
@SuppressWarnings(['Println', 'Instanceof', 'DuplicateNumberLiteral', 'NestedBlockDepth'])
class AnimalsnifferTasksDebug {

    static void printTasks(Project project, String checkTask, String descPrefix) {
        project.afterEvaluate {
            if (project.extensions.findByType(AnimalSnifferExtension).debug) {
                // moving out of configuration phase because android plugin would complain about it
                project.gradle.taskGraph.whenReady {
                    Task check = project.tasks.findByName(checkTask)
                    Set<? extends Task> active = PrintUtils.resolveDependencies(project, check)

                    println '\nRegistered animalsniffer tasks:'
                    project.tasks.withType(AnimalSniffer).all { AnimalSniffer task ->
                        println String.format('\n\t%-35s %-15s %s',
                                task.name,
                                active.contains(task) ? '[default]' : '',
                                task.description.substring(descPrefix.length())
                        )
                        println '\t\treport: ' + project.rootProject.relativePath(
                                task.reports.text.outputLocation.asFile.get())
                        List<String> tasks = []
                        BuildSignatureTask sigTask = null
                        task.dependsOn.each {
                            if (it instanceof TaskProvider) {
                                Task tsk = (it as TaskProvider).get() as Task
                                if (tsk instanceof BuildSignatureTask) {
                                    sigTask = tsk
                                }
                                tasks.add(tsk.name)
                            } else {
                                tasks.add(it as String)
                            }
                        }
                        println "\t\tdepends on: ${tasks.sort().join(', ')}"
                        Set<File> sigs = (sigTask?.signatures ?: task.animalsnifferSignatures).files
                        println "\t\tsignatures: ${sigTask ? '(cached signature)' : ''}\n" +
                                (sigs.empty ? '\t\t\t<No signatures>' : PrintUtils.renderClasspath(project, 3, sigs))
                        println '\t\tclasses:\n' + PrintUtils.renderClasses(
                                3, PrintUtils.inferSourceRoots(task.source), project)
                    }
                    println "\n*use [$DebugSourcesTask.NAME] task to see project sources configuration details\n"
                }
            }
        }
    }
}
