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
                ignoreFailures = true
                incremental = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
            }
        """

        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
        fileFromClasspath('src/main/java/invalid/Sample2.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample2.java')
        //debug()
    }


    def "Check execution"() {

        when: "run task"
        BuildResult result = run('animalsnifferMain')

        then: "task successful"
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS

        then: "errors in both files"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:9  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:14  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])",
                "invalid.Sample2:9  Undefined reference: java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()",
                "invalid.Sample2:9  Undefined reference: Iterable java.nio.file.FileSystem.getFileStores()"
        ]


        when: "run task with no changes"
        result = run('animalsnifferMain')

        then: "task not run"
        result.task(':animalsnifferMain').outcome == TaskOutcome.UP_TO_DATE

        then: "errors file is the same"
        file.exists()
        file.readLines() == [
                "invalid.Sample:9  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:14  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])",
                "invalid.Sample2:9  Undefined reference: java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()",
                "invalid.Sample2:9  Undefined reference: Iterable java.nio.file.FileSystem.getFileStores()"
        ]

        when: "update one file without content change"
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
        result = run('animalsnifferMain')

        then: 'task performed'
        result.task(':animalsnifferMain').outcome == TaskOutcome.UP_TO_DATE

        then: "error file is the same"
        file.exists()
        file.readLines() == [
                "invalid.Sample:9  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:14  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])",
                "invalid.Sample2:9  Undefined reference: java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()",
                "invalid.Sample2:9  Undefined reference: Iterable java.nio.file.FileSystem.getFileStores()"
        ]


        when: "modifying source file"
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/skip/Sample.java')
        result = run('animalsnifferMain')

        then: 'task perfomred'
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS

        then: 'only one file checked'
        file.exists()
        file.readLines() == [
                "invalid.Sample:7  Undefined reference: int Boolean.compare(boolean, boolean)"
        ]


        when: "modifying source file"
        fileFromClasspath('src/main/java/invalid/Sample2.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/skip/Sample2.java')
        result = run('animalsnifferMain')

        then: 'task perfomred'
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS

        then: 'only one file checked'
        file.exists()
        file.readLines() == [
                "invalid.Sample2:9  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }
}