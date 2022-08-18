package ru.vyarus.gradle.plugin.animalsniffer.multilang


import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 17.08.2022
 */
// ignore test on appveyor due to jni problem
@IgnoreIf({ env.containsKey('APPVEYOR') })
class ScalaSourceKitTest extends AbstractKitTest {

    def "Check scala lang support"() {
        setup:
        build """
            plugins {
                id 'scala'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation 'org.scala-lang:scala-library:2.11.12'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/main/scala/invalid/Sample.scala', '/ru/vyarus/gradle/plugin/animalsniffer/scala/invalid/Sample.scala')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.output.contains("2 AnimalSniffer violations were found in 1 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference] invalid.(Sample.scala:12)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.scala:17)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])
""")
    }
}
