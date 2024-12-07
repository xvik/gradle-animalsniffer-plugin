package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.internal.plugins.DefaultPluginManager
import org.gradle.api.internal.plugins.PluginImplementation
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.PluginInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.PluginsModel

import java.lang.reflect.Field

/**
 * Resolve registered plugins.
 *
 * @author Vyacheslav Rusakov
 * @since 30.11.2024
 */
@CompileStatic
@SuppressWarnings('DuplicateStringLiteral')
class PluginsCollector {

    PluginsModel collect(Project project) {
        Map<Class, PluginImplementation> plugins = getPlugins(project)

        PluginsModel model = new PluginsModel()
        plugins.each { cls, plug ->
            if (cls.name.containsIgnoreCase('kotlin')) {
                model.kotlinPlugin = cls
            } else if (cls.package.name == 'com.android.build.gradle') {
                model.androidPlugin = cls
            }
            String id = plug.pluginId ? plug.pluginId.id : cls.simpleName.toLowerCase()
            (id.contains('java') || id.contains('android') || id.contains('kotlin') ? model.interest : model.other)
                    .add(new PluginInfo(id: plug.pluginId?.id,
                            name: plug.pluginId?.name,
                            type: cls))
        }
        model.interest = model.interest.sort()
        model.other = model.other.sort()
        return model
    }

    Map<Class, PluginImplementation> getPlugins(Project project) {
        // hack but the only way to get plugins
        Field f = DefaultPluginManager.getDeclaredField('plugins')
        f.accessible = true
        return f.get(project.pluginManager) as Map<Class, PluginImplementation>
    }
}
