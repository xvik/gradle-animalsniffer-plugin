package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 09.04.2017
 */
class IncrementalCompatibilityCheckKitTest extends AbstractKitTest {
    @Override
    def setup() {

        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
                incremental = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
            }
        """

        fileFromClasspath('src/main/java/invalid/Dep1.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/skip/Dep1.java')
        fileFromClasspath('src/main/java/invalid/Dep2.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/skip/Dep2.java')
        //debug()
    }

    def "Check execution"() {

        when: "run task"
        BuildResult result = runFailed('animalsnifferMain')

        then: "task failed"
        result.task(':animalsnifferMain').outcome == TaskOutcome.FAILED

        then: "task did not run"
        result.output.contains("Animalsniffer can't run in incremental mode when 'ignoreFailures' is enabled because some errors may be missed in this case")
    }
}