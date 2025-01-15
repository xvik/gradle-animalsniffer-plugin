package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.SourceSetInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils
import ru.vyarus.gradle.plugin.animalsniffer.support.reactor.AndroidSourceSetReactor

/**
 * Resolves android source sets.
 *
 * @author Vyacheslav Rusakov
 * @since 24.11.2024
 */
@CompileStatic(TypeCheckingMode.SKIP)
class AndroidSourceSetsInfoCollector {

    List<SourceSetInfo> collect(Project project, boolean collectClasspath) {
        final List<SourceSetInfo> res = []

        new AndroidSourceSetReactor(project).onTarget {
            Set<File> classpath = []
            if (collectClasspath) {
                String resolveConfName = "resolve$it.implementationConfigurationName"
                Configuration configuration = project.configurations.findByName(resolveConfName)
                if (configuration == null) {
                    configuration = project.configurations.create(resolveConfName)
                    // not resolvable configuration (need another configuration to resolve it)
                    configuration.extendsFrom(project.configurations.getByName(it.implementationConfigurationName))
                    configuration.canBeResolved = true
                    configuration.attributes {
                        it.attributes.attribute(Attribute.of('ui', String), 'awt')
                    }
                }

                classpath = PrintUtils.resolve(configuration, "for $it.name android source set")
            }
            res.add(new SourceSetInfo(name: it.name,
                    // DefaultAndroidSourceDirectorySet
                    sourceDirs: it.kotlin.srcDirs,
                    classpath: classpath))
        }
        return res
    }
}
