package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 17.12.2015
 */
class PassKitTest extends AbstractKitTest {

    @Override
    def setup() {

        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
            }
        """

        fileFromClasspath('src/main/java/valid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
        //debug()
    }

    def "Check plugin execution"() {

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
    }

    def "Check plugin execution for gradle 2.14"() {

        when: "run task for gradle 2.14"
        BuildResult result = runVer('2.14', 'check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
    }
}