package ru.vyarus.gradle.plugin.animalsniffer.support.task

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import ru.vyarus.gradle.plugin.animalsniffer.util.TargetType

/**
 * Kotlin multiplatform support (tasks created per each platform compilation).
 *
 * @author Vyacheslav Rusakov
 * @since 11.12.2024
 */
@CompileStatic(TypeCheckingMode.SKIP)
class MultiplatformTaskConfigurationProvider implements AnimalsnifferTaskConfigurationProvider {

    private final String name
    private final String desc
    private final String compileTask
    private final Provider<FileCollection> classes
    private final Provider<FileCollection> classpath
    private final Provider<FileCollection> sources

    // KotlinCompilation
    MultiplatformTaskConfigurationProvider(ObjectFactory objects, ProviderFactory providers,
                                           Object compilation) {
        name = compilation.target.targetName + compilation.name.capitalize()
        desc = "for kotlin platform '${compilation.target.targetName}' compilation '${compilation.name}'"
        compileTask = compilation.compileAllTaskName
        classes = providers.provider { objects.fileCollection().from(compilation.output.classesDirs) }
                as Provider<FileCollection>
        classpath = providers.provider { compilation.compileDependencyFiles } as Provider<FileCollection>
        sources = providers.provider {
            // project.files
            objects.fileCollection().from(
                    compilation.allKotlinSourceSets.collect { it.kotlin.sourceDirectories })
        } as Provider<FileCollection>
    }

    @Override
    TargetType getType() {
        return TargetType.MultiplatformTarget
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
