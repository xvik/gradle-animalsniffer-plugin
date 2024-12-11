package ru.vyarus.gradle.plugin.animalsniffer.support

import groovy.transform.CompileStatic
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.SourceSet

/**
 * Configuration from source set (used for java, kotlin, scala plugins).
 *
 * @author Vyacheslav Rusakov
 * @since 11.12.2024
 */
@CompileStatic
class SourceSetTaskConfigurationProvider implements AnimalsnifferTaskConfigurationProvider {

    private final ObjectFactory objects
    private final String name
    private final String compileTask
    private final Provider<FileCollection> classes
    private final Provider<FileCollection> classpath
    private final Provider<Set<File>> sources

    SourceSetTaskConfigurationProvider(ObjectFactory objects, ProviderFactory providers, SourceSet sourceSet) {
        this.objects = objects
        name = sourceSet.name
        compileTask = sourceSet.classesTaskName
        classes = providers.provider { sourceSet.output as FileCollection }
        classpath = providers.provider { sourceSet.compileClasspath }
        sources = providers.provider { sourceSet.allJava.srcDirs }
    }

    @Override
    String getTargetName() {
        return name
    }

    @Override
    FileCollection getClasses() {
        return classes.get()
    }

    @Override
    FileCollection getCompileClasspath() {
        return classpath.get()
    }

    @Override
    FileCollection getSourceDirs() {
        // project.files
        return objects.fileCollection().from(sources.get())
    }

    @Override
    String getCompileTaskName() {
        return compileTask
    }
}
