package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 15.06.2016
 */
@IgnoreIf({ jvm.isJava9Compatible()})
class FailOldGradleKitTest extends AbstractKitTest {

    def "Check build fail for gradle older 5.0"() {

        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
        """

        when: "run task"
        BuildResult result = runFailedVer('4.0', 'clean')

        then: "task successful"
        result.output.contains("Animalsniffer plugin requires gradle 5.0 or above")
    }
}