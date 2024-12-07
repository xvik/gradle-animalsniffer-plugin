package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic

/**
 * Plugins info (aggregation).
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2024
 */
@CompileStatic
class PluginsModel {

    List<PluginInfo> interest = []
    List<PluginInfo> other = []
    Class kotlinPlugin
    Class androidPlugin
}
