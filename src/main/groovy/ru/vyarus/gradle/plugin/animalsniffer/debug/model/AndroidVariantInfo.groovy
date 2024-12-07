package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic

/**
 * Android variant representation.
 *
 * @author Vyacheslav Rusakov
 * @since 24.11.2024
 */
@CompileStatic
class AndroidVariantInfo {

    String name
    String compileTaskName
    List<SourceSetInfo> sourceSets
    Set<File> classes
    Set<File> classpath
}
