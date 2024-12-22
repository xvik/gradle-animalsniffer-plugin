package ru.vyarus.gradle.plugin.animalsniffer.support.task

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector
import ru.vyarus.gradle.plugin.animalsniffer.util.TargetType

/**
 * Configuration for android variant (used for android application and library plugins).
 *
 * @author Vyacheslav Rusakov
 * @since 11.12.2024
 */
@CompileStatic(TypeCheckingMode.SKIP)
class AndroidTaskConfigurationProvider implements AnimalsnifferTaskConfigurationProvider {

    private final String name
    private final String desc
    private final TaskProvider<AndroidClassesCollector> classesTask
    private final Provider<FileCollection> classes
    private final Provider<FileCollection> classpath
    private final Provider<FileCollection> sources

    // BaseVariant (LibraryVariant or ApplicationVariant)
    AndroidTaskConfigurationProvider(ObjectFactory objects, ProviderFactory providers, Object variant,
                                     TaskProvider<AndroidClassesCollector> classesTask) {
        name = variant.name
        desc = "for '$name' android variant"
        this.classesTask = classesTask
        classes = providers.provider {
            objects.fileCollection().from(classesTask.flatMap { it.outputDirectory })
        }
        classpath = providers.provider { variant.compileClasspath }
        sources = providers.provider {
            // project.files
            objects.fileCollection().from(variant.sources.java.all, variant.sources.kotlin.all)
        }
    }

    @Override
    TargetType getType() {
        return TargetType.AndroidVariant
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
        return classesTask.name
    }
}
