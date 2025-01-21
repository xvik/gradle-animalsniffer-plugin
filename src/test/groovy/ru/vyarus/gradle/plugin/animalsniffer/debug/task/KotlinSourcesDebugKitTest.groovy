package ru.vyarus.gradle.plugin.animalsniffer.debug.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 02.12.2024
 */
class KotlinSourcesDebugKitTest extends AbstractDebugKitTest {

    def "Check simple java project debug"() {
        setup:
        build """
            plugins {
                id 'org.jetbrains.kotlin.jvm' version '2.0.21'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation platform('org.jetbrains.kotlin:kotlin-bom') 
                implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8' 
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/main/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
//        debug()

        when: "run task"
        BuildResult result = run('printAnimalsnifferSourceInfo')

        then: "task successful"
        result.task(':printAnimalsnifferSourceInfo').outcome == TaskOutcome.SUCCESS

        then: "report validation"
        equalWithDiff(extractReport(result), readReport("repo"))
        !result.output.contains('WARN:')
    }
}
