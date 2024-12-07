package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic

/**
 * Source set representation.
 *
 * @author Vyacheslav Rusakov
 * @since 24.11.2024
 */
@CompileStatic
class SourceSetInfo {

    String name
    Set<File> sourceDirs
    Set<File> classes
    Set<File> classpath
}
