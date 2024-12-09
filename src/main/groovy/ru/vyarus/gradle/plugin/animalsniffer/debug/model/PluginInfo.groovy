package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull
import ru.vyarus.gradle.plugin.animalsniffer.debug.util.PrintUtils

/**
 * Plugin representation.
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2024
 */
@CompileStatic
class PluginInfo implements Comparable<PluginInfo> {

    String id
    String name
    Class type

    @Override
    int compareTo(@NotNull PluginInfo o) {
        // plugins could be named the same (e.g. android library and internal android library)
        String simpleName = PrintUtils.simpleName(type)
        String oSimpleName = PrintUtils.simpleName(o.type)

        return simpleName == oSimpleName ? type.name <=> o.type.name : simpleName <=> oSimpleName
    }
}
