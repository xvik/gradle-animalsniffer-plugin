package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 08.04.2017
 */
class IncrementalModeForDependantKitTest extends AbstractKitTest {

    @Override
    def setup() {

        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
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

        then: "error in one file"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "invalid.Dep2:7  Undefined reference: int Boolean.compare(boolean, boolean)"
        ]


        when: "run task again"
        result = runFailed('animalsnifferMain')

        then: "task failed"
        result.task(':animalsnifferMain').outcome == TaskOutcome.FAILED

        then: "same errors appear again"
        file.exists()
        file.readLines() == [
                "invalid.Dep2:7  Undefined reference: int Boolean.compare(boolean, boolean)"
        ]


        when: "fixing errors"
        fileFromClasspath('src/main/java/invalid/Dep2.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/skip/Dep2_1.java')
        result = run('animalsnifferMain')

        then: 'task successful'
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS


        when: "modifying one source file"
        fileFromClasspath('src/main/java/invalid/Dep1.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/skip/Dep1_1.java')
        result = runFailed('animalsnifferMain', '--info')

        then: 'task failed'
        result.task(':animalsnifferMain').outcome == TaskOutcome.FAILED

        then: 'only one file checked'
        result.output.contains('[animalsniffer] Incremental mode: checking only 1 changed files')
        file.exists()
        file.readLines() == [
                "invalid.Dep1:9  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }
}