package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic

/**
 * Compile tasks info (aggregation).
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2024
 */
@CompileStatic
class CompileTasksModel {
    // structure only
    List<CompileTaskInfo> allTasks

    List<CompileTaskInfo> javaTasks
    List<CompileTaskInfo> kotlinTasks
}
