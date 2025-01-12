package ru.vyarus.gradle.plugin.animalsniffer.worker

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.workers.WorkParameters

/**
 * Animalsniffer build signature parameters.
 *
 * @author Vyacheslav Rusakov
 * @since 11.01.2025
 */
interface BuildParameters extends WorkParameters {

    /**
     * @return classes to add to signature
     */
    ListProperty<File> getPath()

    /**
     * @return existing signatures to merge
     */
    SetProperty<File> getSignatures()

    /**
     * @return include classes
     */
    SetProperty<String> getInclude()

    /**
     * @return exclude classes
     */
    SetProperty<String> getExclude()

    /**
     * @return output signature file
     */
    Property<File> getOutput()
}
