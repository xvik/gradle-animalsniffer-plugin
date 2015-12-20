package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.api.plugins.quality.CodeQualityExtension

/**
 * Animal sniffer plugin extension. Use 'sourceSets' to define target source set (all by default).
 * Use 'toolVersion' to override default version. 'ignoreFailures' prevents failures on error.
 *
 * @author Vyacheslav Rusakov
 * @since 13.12.2015
 */
class AnimalSnifferExtension extends CodeQualityExtension {

    /**
     * Annotation class name used to disable check for annotated class/method/field.
     */
    String annotation
}
