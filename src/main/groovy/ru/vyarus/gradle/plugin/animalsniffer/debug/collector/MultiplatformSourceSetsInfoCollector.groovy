package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.KotlinCompilationInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.KotlinTargetInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.SourceSetInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils
import ru.vyarus.gradle.plugin.animalsniffer.support.reactor.MultiplatformSourceSetsReactor
import ru.vyarus.gradle.plugin.animalsniffer.support.reactor.MultiplatformTargetsReactor

/**
 * Resolve kotlin multiplatform source sets and targets.
 *
 * @author Vyacheslav Rusakov
 * @since 24.11.2024
 */
@CompileStatic(TypeCheckingMode.SKIP)
class MultiplatformSourceSetsInfoCollector {

    List<SourceSetInfo> collect(Project project) {
        List<SourceSetInfo> res = []
        new MultiplatformSourceSetsReactor(project).onTarget {
            res.add(new SourceSetInfo(name: it.name, sourceDirs: it.kotlin.sourceDirectories.files))
        }
        return res
    }

    List<KotlinTargetInfo> collectTargets(Project project, boolean collectClasspath) {
        List<KotlinTargetInfo> res = []

        new MultiplatformTargetsReactor(project).onTarget { target ->
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
