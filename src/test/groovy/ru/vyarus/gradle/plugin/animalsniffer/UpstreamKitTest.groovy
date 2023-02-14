package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 16.12.2019
 */
class UpstreamKitTest extends AbstractKitTest {

    public static final String GRADLE_VERSION = '8.0'

    def "Check violation detection without cache task"() {
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
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = runVer(GRADLE_VERSION, 'check', '--warning-mode', 'all')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.output.contains("2 AnimalSniffer violations were found in 1 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference] invalid.(Sample.java:11)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.java:16)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])
""")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:11  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:16  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }

    def "Check configuration cache support"() {

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
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }
        """

        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
        //debug()

        when: "run task"
        BuildResult result = runFailedVer(GRADLE_VERSION, 'check', '--configuration-cache')

        then: "task successful"
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS
        // testKit is incompatible with configuration cache, but I can check number of errors!
        result.output.contains('1 problem was found storing the configuration cache.\n' +
                '- Gradle runtime: support for using a Java agent with TestKit builds is not yet implemented with the configuration cache.')

    }
}
