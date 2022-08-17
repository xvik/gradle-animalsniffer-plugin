package ru.vyarus.gradle.plugin.animalsniffer.multilang

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 17.08.2022
 */
class GroovySourceKitTest extends AbstractKitTest {

    def "Check groovy lang support"() {
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
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.output.contains("2 AnimalSniffer violations were found in 1 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference] invalid.(Sample.groovy:13)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.groovy:18)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])
""")
    }
}
