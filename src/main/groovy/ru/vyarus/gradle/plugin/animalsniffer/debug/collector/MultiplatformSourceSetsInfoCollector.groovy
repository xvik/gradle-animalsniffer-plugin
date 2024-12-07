package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.KotlinCompilationInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.SourceSetInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.KotlinTargetInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils

/**
 * Resolve kotlin multiplatform source sets and targets.
 *
 * @author Vyacheslav Rusakov
 * @since 24.11.2024
 */
@CompileStatic(TypeCheckingMode.SKIP)
class MultiplatformSourceSetsInfoCollector {

    private static final String KOTLIN_EXTENSION = 'kotlin'

    List<SourceSetInfo> collect(Project project) {
        // org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
        NamedDomainObjectContainer<KotlinSourceSet> sourceSets = project.extensions
                .getByName(KOTLIN_EXTENSION).sourceSets
        List<SourceSetInfo> res = []
        sourceSets.each {
            res.add(new SourceSetInfo(name: it.name, sourceDirs: it.kotlin.sourceDirectories.files))
        }
        return res
    }

    List<KotlinTargetInfo> collectTargets(Project project, boolean collectClasspath) {
        // org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
        NamedDomainObjectCollection<KotlinTarget> targets = project.extensions.getByName(KOTLIN_EXTENSION).targets
        List<KotlinTargetInfo> res = []
        targets.each { target ->
            List<KotlinCompilationInfo> ci = []
            target.compilations.each {
                Set<File> cp = []
                if (collectClasspath) {
                    // using extra configuration to specify resolution attributes
                    Configuration configuration = project.configurations
                            .create("resolve$it.implementationConfigurationName")
                    configuration.extendsFrom(project.configurations.getByName(it.compileDependencyConfigurationName))
                    configuration.canBeResolved = true
                    configuration.attributes {
                        attributes.attribute(Attribute.of('ui', String), 'awt')
                    }

                    cp = PrintUtils.resolve(configuration, "for kotlin compilation $it.name")
                }
                ci.add(new KotlinCompilationInfo(name: it, compileTaskName: it.compileAllTaskName,
                        classes: it.output.classesDirs.files,
                        sourceSets: it.allKotlinSourceSets.collect {
                            new SourceSetInfo(name: it.name, sourceDirs: it.kotlin.sourceDirectories.files)
                        },
                        classpath: cp,
                        associatedCompilations: it.allAssociatedCompilations*.name))
            }
            res.add(new KotlinTargetInfo(name: target.targetName, platform: target.platformType.name, compilations: ci))
        }
        return res
    }
}
