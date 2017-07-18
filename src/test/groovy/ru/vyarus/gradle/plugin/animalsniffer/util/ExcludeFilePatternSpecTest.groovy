package ru.vyarus.gradle.plugin.animalsniffer.util

import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov
 * @since 18.07.2017
 */
class ExcludeFilePatternSpecTest extends Specification {

    def "Check pattern matching"() {

        setup:
        ExcludeFilePatternSpec spec = new ExcludeFilePatternSpec([
                'slf4j', // direct match
                'slf4j-*' // wildcard
        ])

        expect: "correct exclusion"
        !spec.isSatisfiedBy(new File('slf4j.jar'))
        !spec.isSatisfiedBy(new File('slf4j-1.1.0.jar'))
        !spec.isSatisfiedBy(new File('slf4j-parent-1.1.0.jar'))
        spec.isSatisfiedBy(new File('other-1.1.0.jar'))
    }
}