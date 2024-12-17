package ru.vyarus.gradle.plugin.animalsniffer.util

import groovy.transform.CompileStatic

/**
 * Represents type of the task.
 *
 * @author Vyacheslav Rusakov
 * @since 13.12.2024
 */
@CompileStatic
@SuppressWarnings('FieldName')
enum TargetType {
    /**
     * Java source set (simple kotlin, scala etc.) - activated with JavaBasePlugin.
     */
    SourceSet,
    /**
     * Kotlin multiplatform (even when java plugin is active, multiplatform supersede source set tasks).
     * The only exception - android platform (superseded by android plugin).
     */
    MultiplatformTarget,
    /**
     * Android plugin support - android variant.
     */
    AndroidVariant
}
