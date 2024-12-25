package ru.vyarus.gradle.plugin.animalsniffer.debug

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import ru.vyarus.gradle.plugin.animalsniffer.AnimalSniffer
import ru.vyarus.gradle.plugin.animalsniffer.AnimalSnifferPlugin
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils
import ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask

/**
 * Prints registered animalsniffer tasks. In android project, it would depend on android build tasks because
 * it is required for compilation target information collection.
 *
 * @author Vyacheslav Rusakov
 * @since 13.12.2024
 */
@CompileStatic
@SuppressWarnings(['Println', 'Instanceof', 'DuplicateNumberLiteral', 'NestedBlockDepth'])
class PrintAnimalsnifferTasksTask extends DefaultTask {

    public static final String NAME = 'printAnimalsnifferTasks'

    @TaskAction
    void printTasks() {
        // moving out of configuration phase because android plugin would complain about it
        Task check = project.tasks.findByName(AnimalSnifferPlugin.CHECK_TASK)
        Set<? extends Task> active = PrintUtils.resolveDependencies(project, check)

        project.tasks.withType(AnimalSniffer).stream()
                // sort for predictable order
                .sorted { task1, task2 -> (task1.name <=> task2.name) }
                .collect { AnimalSniffer task ->
                    println String.format('\n\n\t%-35s %-15s %s',
                            task.name,
                            active.contains(task) ? '[default]' : '',
                            task.description.substring(AnimalSnifferPlugin.AS_CHECK_TASK_DESCR.length() + 1)
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
                    println '\t\tclasses:\n' + (task.classesDirs.empty ? '\t\t\t<empty>' : PrintUtils.renderClasses(
                            3, task.classesDirs, project))
                    println '\t\tsources:\n' + PrintUtils.renderSources(
                            3, task.sourcesDirs.files, project, true)
                }
        println "\n*use [$PrintAnimalsnifferSourceInfoTask.NAME] task to see project sources configuration details\n"
    }

}
