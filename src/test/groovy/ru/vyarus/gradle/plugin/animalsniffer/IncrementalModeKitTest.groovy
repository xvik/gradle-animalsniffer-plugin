package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome


/**
 * @author Vyacheslav Rusakov
 * @since 06.04.2017
 */
class IncrementalModeKitTest extends AbstractKitTest {

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

        fileFromClasspath('src/main/java/skip/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/skip/Sample.java')
        fileFromClasspath('src/main/java/skip/Sample2.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/skip/Sample2.java')
        //debug()
    }


    def "Check execution"() {

        when: "run task"
        BuildResult result = run('animalsnifferMain')

        then: "task successful"
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS

        then: "no errors"
        File file = file('/build/reports/animalsniffer/main.text')


        when: "run task with no changes"
        result = run('animalsnifferMain')

        then: "task not run"
        result.task(':animalsnifferMain').outcome == TaskOutcome.UP_TO_DATE


        when: "update one file"
        fileFromClasspath('src/main/java/skip/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/skip/Sample_1.java')
        result = runFailed('animalsnifferMain', '--info')

        then: "failed"
        result.task(':animalsnifferMain').outcome == TaskOutcome.FAILED
        result.output.contains('[animalsniffer] Incremental mode: checking only 1 changed files')

        then: "error file ok"
        file.exists()
        file.readLines() == [
                "skip.Sample:7  Undefined reference: int Boolean.compare(boolean, boolean)"
        ]


        when: "run one more time"
        result = runFailed('animalsnifferMain', '--info')

        then: "failed"
        result.task(':animalsnifferMain').outcome == TaskOutcome.FAILED
        result.output.contains('[animalsniffer] Incremental mode: checking only 1 changed files')

        then: "error file is the same"
        file.exists()
        file.readLines() == [
                "skip.Sample:7  Undefined reference: int Boolean.compare(boolean, boolean)"
        ]
    }
}