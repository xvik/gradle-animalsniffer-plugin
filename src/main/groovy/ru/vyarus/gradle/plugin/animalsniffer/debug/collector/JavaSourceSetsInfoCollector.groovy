package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import groovy.transform.CompileStatic
import org.gradle.api.Project
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.SourceSetInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils
import ru.vyarus.gradle.plugin.animalsniffer.support.reactor.JavaSourceSetsReactor

/**
 * Resolve java source sets.
 *
 * @author Vyacheslav Rusakov
 * @since 24.11.2024
 */
@CompileStatic
class JavaSourceSetsInfoCollector {

    List<SourceSetInfo> collect(Project project, boolean collectClasspath) {
        List<SourceSetInfo> res = []

        new JavaSourceSetsReactor(project).onTarget {
            res.add(new SourceSetInfo(name: it.name, sourceDirs: PrintUtils.inferSourceRoots(it.allJava.asFileTree),
                    classes: it.output.files,
                    classpath: collectClasspath ? it.compileClasspath.files : [] as Set))
        }
        return res
    }
}
