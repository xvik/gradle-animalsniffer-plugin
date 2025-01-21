package ru.vyarus.gradle.plugin.animalsniffer.debug.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 02.12.2024
 */
class GroovySourcesDebugKitTest extends AbstractDebugKitTest {

    def "Check simple groovy project debug"() {
        setup:
        build """
            plugins {
                id 'groovy'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation localGroovy()
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/main/groovy/invalid/Sample.groovy', '/ru/vyarus/gradle/plugin/animalsniffer/groovy/invalid/Sample.groovy')
//        debug()

        when: "run task"
        BuildResult result = run('printAnimalsnifferSourceInfo')

        then: "task successful"
        result.task(':printAnimalsnifferSourceInfo').outcome == TaskOutcome.SUCCESS

        then: "report validation (no special groovy treatment)"
        equalWithDiff(extractReport(result), readReport("repo"))
        !result.output.contains('WARN:')
    }
}
