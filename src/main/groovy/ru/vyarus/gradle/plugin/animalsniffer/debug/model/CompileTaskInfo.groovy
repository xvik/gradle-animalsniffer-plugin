package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic

/**
 * Compile task representation (java or kotlin).
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2024
 */
@CompileStatic
class CompileTaskInfo extends TaskInfo {

    Set<File> sourceDirs
    Set<File> classes
    Set<File> classpath
}
