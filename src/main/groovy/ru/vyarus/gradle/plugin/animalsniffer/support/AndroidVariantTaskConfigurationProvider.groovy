package ru.vyarus.gradle.plugin.animalsniffer.support

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
import ru.vyarus.gradle.plugin.animalsniffer.util.AndroidClassesCollector

/**
 * Configuration for android variant (used for android application and library plugins).
 *
 * @author Vyacheslav Rusakov
 * @since 11.12.2024
 */
@CompileStatic(TypeCheckingMode.SKIP)
class AndroidVariantTaskConfigurationProvider implements AnimalsnifferTaskConfigurationProvider {

    private final ObjectFactory project
    private final String name
    private final TaskProvider<AndroidClassesCollector> classesTask
    private final Provider<FileCollection> classpath
    private final Provider<FileCollection> sources

    // BaseVariant (LibraryVariant or ApplicationVariant)
    AndroidVariantTaskConfigurationProvider(ObjectFactory objects, ProviderFactory providers, Object variant,
                                            TaskProvider<AndroidClassesCollector> classesTask) {
        this.project = objects
        this.classesTask = classesTask
        name = variant.name
        classpath = providers.provider { variant.compileClasspath }
        sources = providers.provider {
            // project.files
            project.fileCollection().from(variant.sources.java.all, variant.sources.kotlin.all)
        }
    }

    @Override
    String getTargetName() {
        return name
    }

    @Override
    FileCollection getClasses() {
        // project.files
        return project.fileCollection().from(classesTask.flatMap { it.outputDirectory })
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
