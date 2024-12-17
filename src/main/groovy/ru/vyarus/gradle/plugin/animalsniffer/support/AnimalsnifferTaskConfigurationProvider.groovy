package ru.vyarus.gradle.plugin.animalsniffer.support

import groovy.transform.CompileStatic
import org.gradle.api.file.FileCollection
import ru.vyarus.gradle.plugin.animalsniffer.util.TargetType

/**
 * Animalsniffer task configuration unification interface.
 *
 * @author Vyacheslav Rusakov
 * @since 11.12.2024
 */
@CompileStatic
interface AnimalsnifferTaskConfigurationProvider {

    /**
     * @return type of target
     */
    TargetType getType()

    /**
     * @return target name (used for task and report name)
     */
    String getTargetName()

    /**
     * @return task source description
     */
    String getDescription()

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
