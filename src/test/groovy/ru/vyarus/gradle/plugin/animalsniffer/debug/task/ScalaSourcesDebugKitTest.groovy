package ru.vyarus.gradle.plugin.animalsniffer.debug.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 02.12.2024
 */
class ScalaSourcesDebugKitTest extends AbstractDebugKitTest {

    def "Check simple scala project debug"() {
        setup:
        build """
            plugins {
                id 'scala'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation 'org.scala-lang:scala-library:2.13.13'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/main/scala/invalid/Sample.scala', '/ru/vyarus/gradle/plugin/animalsniffer/scala/invalid/Sample.scala')
//        debug()

        when: "run task"
        BuildResult result = run('debugAnimalsnifferSources')

        then: "task successful"
        result.task(':debugAnimalsnifferSources').outcome == TaskOutcome.SUCCESS

        then: "report validation"
        extractReport(result) == readReport("repo")
        !result.output.contains('WARN:')
    }
}
