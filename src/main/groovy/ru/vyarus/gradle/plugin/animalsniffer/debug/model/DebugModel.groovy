package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic

/**
 * Root debug info model.
 *
 * @author Vyacheslav Rusakov
 * @since 27.11.2024
 */
@CompileStatic
class DebugModel {

    PluginsModel plugins
    CompileTasksModel compileTasks
    List<SourceSetInfo> java
    // multiplatform
    List<SourceSetInfo> kotlin
    List<KotlinTargetInfo> kotlinTargets

    boolean androidLibrary
    List<SourceSetInfo> android
    List<AndroidVariantInfo> androidVariants

    Map<String, String> getKotlinTargetSourceSetsIndex() {
        Map<String, String> res = null
        if (kotlinTargets) {
            res = [:]
            kotlinTargets.each { target ->
                target.compilations.each { comp ->
                    comp.sourceSets.each {
                        res.put(it.name, "consumed by $comp.name".toString())
                    }
                }
            }
        }
        return res
    }

    Map<String, String> getAndroidVariantsSourceSetsIndex() {
        Map<String, String> res = null
        if (androidVariants) {
            res = [:]
            androidVariants.each { variant ->
                variant.sourceSets.each { s ->
                    res.put(s.name, ("consumed by ${variant.testType ? "$variant.testType component" : 'variant'} "
                            + "$variant.name").toString())
                }
            }
        }
        return res
    }
}
