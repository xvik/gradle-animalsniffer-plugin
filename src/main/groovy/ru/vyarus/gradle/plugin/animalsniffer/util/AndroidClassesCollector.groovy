package ru.vyarus.gradle.plugin.animalsniffer.util

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

/**
 * Android classes collector.
 *
 * @author Cesar Munoz
 * @since 14.11.2024
 */
@CompileStatic
abstract class AndroidClassesCollector extends DefaultTask {

    static String computeTaskName(String prefix) {
        prefix + 'AnimalsnifferClassesCollector'
    }

    @InputFiles
    abstract ListProperty<RegularFile> getJarFiles()

    @InputFiles
    abstract ListProperty<Directory> getClassesDirs()

    @InputFiles
    abstract SetProperty<File> getMultiplatformSourceDirs()

    @TaskAction
    @SuppressWarnings('EmptyMethodInAbstractClass')
    void execute() {
        // no body
    }
}
