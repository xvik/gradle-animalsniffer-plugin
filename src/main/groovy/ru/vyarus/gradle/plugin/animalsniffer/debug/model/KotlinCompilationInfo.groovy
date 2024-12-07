package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic

/**
 * Kotlin compilation representation.
 *
 * @author Vyacheslav Rusakov
 * @since 25.11.2024
 */
@CompileStatic
class KotlinCompilationInfo {

    String name
    String compileTaskName
    List<SourceSetInfo> sourceSets
    Set<File> classes
    Set<File> classpath
    Set<String> associatedCompilations
}
