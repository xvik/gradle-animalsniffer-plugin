package ru.vyarus.gradle.plugin.animalsniffer.debug.model

import groovy.transform.CompileStatic

/**
 * Kotlin target representation.
 *
 * @author Vyacheslav Rusakov
 * @since 25.11.2024
 */
@CompileStatic
class KotlinTargetInfo {

    String name
    String platform
    List<KotlinCompilationInfo> compilations
}
