package ru.vyarus.gradle.plugin.animalsniffer.util

import groovy.transform.CompileStatic
import org.gradle.api.specs.Spec

import java.util.regex.Pattern

/**
 * Spec excludes files matching provided pattern. Accept regexp as input.
 * Input pattern could contain '*' (for simpler pattern definition), which will be replaced with '.+'.
 * <p>
 * NOTE: as it assumed to be used with classpath only, file extension is not checked!
 *
 * @author Vyacheslav Rusakov
 * @since 18.07.2017
 */
@CompileStatic
class ExcludeFilePatternSpec implements Spec<File> {

    private final Pattern pattern

    ExcludeFilePatternSpec(Collection<String> desc) {
        this.pattern = buildPattern(desc)
    }

    @Override
    boolean isSatisfiedBy(File element) {
        String name = element.name[0..element.name.lastIndexOf('.') - 1]
        return pattern == null ||
                !pattern.matcher(name).matches()
    }

    private static Pattern buildPattern(Collection<String> desc) {
        if (!desc) {
            return null
        }
        List<String> res = desc.collect { "(${it.replace('*', '.+')})".toString() }
        return Pattern.compile(res.join('|'))
    }
}
