package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 10.07.2018
 */
class RetrolambdaCompatibilityTest extends AbstractKitTest {

    def "Check no signatures defined"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
                id 'me.tatarka.retrolambda' version '3.7.0'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral() }
            dependencies {
                signature 'net.sf.androidscents.signature:android-api-level-21:5.0.1_r2@signature'
            }
        """
        fileFromClasspath('src/main/java/retrolambda/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/retrolambda/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "errors found"
        result.output.contains("AnimalSniffer violations were found")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "retrolambda.Sample:10  Undefined reference: java.util.stream.Stream java.util.List.stream()",
                "retrolambda.Sample:10  Undefined reference: java.util.function.Consumer" ,
                "retrolambda.Sample:10  Undefined reference: void java.util.stream.Stream.forEach(java.util.function.Consumer)"
        ]
    }
}
