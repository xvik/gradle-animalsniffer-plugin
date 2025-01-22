package ru.vyarus.gradle.plugin.animalsniffer.support.task

import groovy.transform.CompileStatic
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.SourceSet
import ru.vyarus.gradle.plugin.animalsniffer.util.TargetType

/**
 * Configuration from source set (used for java, kotlin, scala plugins).
 *
 * @author Vyacheslav Rusakov
 * @since 11.12.2024
 */
@CompileStatic
class JavaTaskConfigurationProvider implements AnimalsnifferTaskConfigurationProvider {

    private final String name
    private final String desc
    private final String compileTask
    private final Provider<FileCollection> classes
    private final Provider<FileCollection> classpath
    private final Provider<FileCollection> sources

    JavaTaskConfigurationProvider(ObjectFactory objects, ProviderFactory providers, SourceSet sourceSet) {
        name = sourceSet.name
        desc = "for '$name' source set"
        compileTask = sourceSet.classesTaskName
        classes = providers.provider { sourceSet.output.classesDirs }
        classpath = providers.provider { sourceSet.compileClasspath }
        sources = providers.provider { objects.fileCollection().from(sourceSet.allJava.srcDirs) }
                as Provider<FileCollection>
    }

    @Override
    TargetType getType() {
        return TargetType.Java
    }

    @Override
    String getTargetName() {
        return name
    }

    @Override
    String getDescription() {
        return desc
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
        return sources.get()
    }

    @Override
    String getCompileTaskName() {
        return compileTask
    }
}
