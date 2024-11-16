package ru.vyarus.gradle.plugin.animalsniffer.util

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Android classes collector.
 *
 * @author Cesar Munoz
 * @since 14.11.2024
 */
@CompileStatic
abstract class AndroidClassesCollector extends DefaultTask {

    @InputFiles
    abstract ListProperty<RegularFile> getJarFiles()

    @InputFiles
    abstract ListProperty<Directory> getClassesDirs()

    @OutputDirectory
    abstract DirectoryProperty getOutputDirectory()

    protected AndroidClassesCollector() {
        outputDirectory.value(project.layout.buildDirectory.dir('intermediates/animal_sniffer/' + name))
    }

    @TaskAction
    void execute() {
        project.sync {
            it.from(classesDirs)
            it.into(outputDirectory)
        }
    }
}
