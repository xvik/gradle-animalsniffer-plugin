package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 19.12.2015
 */
class AnnKitTest extends AbstractKitTest {

    def "Check default annotation detection"() {
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
                compile "org.codehaus.mojo:animal-sniffer-annotations:1.14"
            }

        """
        fileFromClasspath('src/main/java/ann/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/ann/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 1 violations"
        result.output.contains("1 AnimalSniffer violations were found in 1 files")
        result.output.replaceAll('\r', '').contains(
"""[Undefined reference] ann.(Sample.java:17)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])""")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "ann.Sample:17  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }


    def "Check enclosing class annotation detection for inner class"() {
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
                compile "org.codehaus.mojo:animal-sniffer-annotations:1.14"
            }

        """
        fileFromClasspath('src/main/java/ann/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/ann4inner/Sample.java')
        fileFromClasspath('src/main/java/ann/Sample2.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/ann4inner/Sample2.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found no violations"
        !result.output.contains("AnimalSniffer violations were found")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        !file.exists()
    }


    def "Check custom annotation detection"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
                annotation = 'custann.Ann'
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
            }

        """

        fileFromClasspath('src/main/java/custann/Ann.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/custann/Ann.java')
        fileFromClasspath('src/main/java/custann/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/custann/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 1 violations"
        result.output.contains("1 AnimalSniffer violations were found in 1 files")
        result.output.replaceAll('\r', '').contains(
"""[Undefined reference] custann.(Sample.java:15)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])""")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "custann.Sample:15  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }
}