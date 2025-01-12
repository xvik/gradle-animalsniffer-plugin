package ru.vyarus.gradle.plugin.animalsniffer.worker

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.workers.WorkParameters

/**
 * Animalsniffer check worker parameters.
 *
 * @author Vyacheslav Rusakov
 * @since 10.01.2025
 */
interface CheckParameters extends WorkParameters {

    /**
     * @return animalsniffer signature files
     */
    ListProperty<File> getSignatures()

    /**
     * @return classpath
     */
    ListProperty<File> getClasspath()

    /**
     * @return all classes to be checked (order is important)
     */
    ListProperty<File> getClasses()

    /**
     * @return directories with sources
     */
    SetProperty<File> getSourceDirs()

    /**
     * @return alternative annotations
     */
    SetProperty<String> getAnnotations()

    /**
     * @return ignored classes
     */
    SetProperty<String> getIgnored()

    /**
     * @return true to continue build even if failures found
     */
    Property<Boolean> getIgnoreErrors()

    /**
     * @return output report path (file used to exchange errors between task and worker)
     */
    Property<File> getReportOutput()
}
