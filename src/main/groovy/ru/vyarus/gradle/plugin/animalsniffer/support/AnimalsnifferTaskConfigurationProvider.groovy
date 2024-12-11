package ru.vyarus.gradle.plugin.animalsniffer.support

import groovy.transform.CompileStatic
import org.gradle.api.file.FileCollection

/**
 * Animalsniffer task configuration unification interface.
 *
 * @author Vyacheslav Rusakov
 * @since 11.12.2024
 */
@CompileStatic
interface AnimalsnifferTaskConfigurationProvider {

    /**
     * @return target name (used for task name, description and report name)
     */
    String getTargetName()

    /**
     * @return classes directories descriptor (analysis source)
     */
    FileCollection getClasses()

    /**
     * @return dependencies (classpath)
     */
    FileCollection getCompileClasspath()

    /**
     * @return source directories
     */
    FileCollection getSourceDirs()

    /**
     * @return task to depend on in order to be sure that classes compiled (otherwise nothing to analyze)
     */
    String getCompileTaskName()
}
