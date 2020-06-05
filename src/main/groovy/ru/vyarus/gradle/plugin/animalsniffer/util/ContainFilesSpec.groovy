package ru.vyarus.gradle.plugin.animalsniffer.util

import groovy.transform.CompileStatic
import org.gradle.api.specs.Spec

/**
 * Matches only files from provided collection. Useful for exclude/include logic on file collection:
 * <code>fileCollection.filter new ContainFilesSpec(someFiles)</code>
 * to preserve only files containing in someFiles collection (include). And the opposite:
 * <code>fileCollection.filter new NotSpec<File>(new ContainFilesSpec(someFiles))</code>
 * to exclude all files in someFiles collection (exclude).
 *
 * @author Vyacheslav Rusakov
 * @since 24.05.2017
 */
@CompileStatic
class ContainFilesSpec implements Spec<File> {

    private final Set<File> files

    ContainFilesSpec(Set<File> files) {
        this.files = files
    }

    @Override
    boolean isSatisfiedBy(File element) {
        return files.contains(element)
    }
}
