package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull

/**
 * Task representation.
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2024
 */
@CompileStatic
class TaskInfo implements Comparable<TaskInfo> {

    String name
    Class type
    List<TaskInfo> dependsOn = []

    @Override
    int compareTo(@NotNull TaskInfo o) {
        int res = type.simpleName <=> o.type.simpleName
        if (res == 0) {
            res = name <=> o.name
        }
        return res
    }

    @Override
    String toString() {
        return 'TaskInfo{' +
                'name=\'' + name + '\'' +
                ', type=' + type +
                '}'
    }
}
