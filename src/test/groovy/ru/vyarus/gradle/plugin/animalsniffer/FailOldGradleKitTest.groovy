package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov
 * @since 15.06.2016
 */
class FailOldGradleKitTest extends AbstractKitTest {

    def "Check build fail for gradle older 2.14"() {

        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
        """

        when: "run task"
        BuildResult result = runFailedVer('2.13', 'clean')

        then: "task successful"
        result.output.contains("Animalsniffer plugin requires gradle 2.14 or above")
    }
}