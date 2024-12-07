package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import com.android.build.gradle.api.AndroidSourceSet
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.SourceSetInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils

/**
 * Resolves android source sets.
 *
 * @author Vyacheslav Rusakov
 * @since 24.11.2024
 */
@CompileStatic(TypeCheckingMode.SKIP)
class AndroidSourceSetsInfoCollector {

    List<SourceSetInfo> collect(Project project, boolean collectClasspath) {
        // com.android.build.gradle.internal.dsl.BaseAppModuleExtension
        NamedDomainObjectContainer<AndroidSourceSet> sourceSets = project.extensions.getByName('android').sourceSets
        List<SourceSetInfo> res = []
        sourceSets.each {
            // DefaultAndroidSourceDirectorySet

            Set<File> classpath = []
            if (collectClasspath) {
                Configuration configuration = project.configurations
                        .create("resolve$it.implementationConfigurationName")
                // not resolvable configuration (need another configuration to resolve it)
                configuration.extendsFrom(project.configurations.getByName(it.implementationConfigurationName))
                configuration.canBeResolved = true
                configuration.attributes {
                    attributes.attribute(Attribute.of('ui', String), 'awt')
                }

                classpath = PrintUtils.resolve(configuration, "for $it.name android source set")
            }
            res.add(new SourceSetInfo(name: it.name, sourceDirs: it.kotlin.srcDirs, classpath: classpath))
        }
        return res
    }
}
