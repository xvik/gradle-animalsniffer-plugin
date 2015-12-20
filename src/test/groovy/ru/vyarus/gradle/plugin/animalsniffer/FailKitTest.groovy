package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 13.12.2015
 */
class FailKitTest extends AbstractKitTest {

    def "Check violation detection"() {
        setup:
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
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.standardError.contains("2 AnimalSniffer violations were found in 1 files")
        result.standardError.contains("Undefined reference:")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:9  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:14  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }

    def "Check multiple signatures"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral() }
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4'
            }
        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.standardError.contains("2 AnimalSniffer violations were found in 1 files")
        result.standardError.contains("Undefined reference:")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:9  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:14  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }

    def "Check no signatures defined"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral() }
        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "animalsniffer skipepd"
        !result.standardError.contains("AnimalSniffer violations were found")

        then: "no report"
        !file('/build/reports/animalsniffer/main.text').exists()
    }

    def "Check only tests configured"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
                sourceSets = [sourceSets.test]
            }

            repositories { mavenCentral() }
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
            }
        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.UP_TO_DATE

        then: "found no violations"
        !result.standardError.contains("AnimalSniffer violations were found")

        then: "report correct"
        !file('/build/reports/animalsniffer/test.text').exists()
    }

    def "Check report overridden"() {
        setup:
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
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        run('animalsnifferMain')
        run('animalsnifferMain', '--rerun-tasks')

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:9  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:14  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }

    def "Check up to date"() {
        setup:
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
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        run('animalsnifferMain')
        BuildResult result = run('animalsnifferMain')

        then: "task not executed on second time"
        result.task(':animalsnifferMain').outcome == TaskOutcome.UP_TO_DATE
    }
}