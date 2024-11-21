package ru.vyarus.gradle.plugin.animalsniffer.multilang

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 17.08.2022
 */
class KotlinSourceKitTest extends AbstractKitTest {

    def "Check kotlin lang support"() {
        setup:
        build """
            plugins {
                id 'org.jetbrains.kotlin.jvm' version '2.0.21'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation platform('org.jetbrains.kotlin:kotlin-bom') 
                implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8' 
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/main/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.output.contains("2 AnimalSniffer violations were found in 1 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference] invalid.(Sample.kt:11)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.kt:16)
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

    def "Check kotlin multiplatform support"() {
        setup:
        build """
            plugins {
                id 'org.jetbrains.kotlin.multiplatform' version '2.0.21'
                id 'ru.vyarus.animalsniffer'
            }
            
            kotlin {
                jvm().withJava()
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation platform('org.jetbrains.kotlin:kotlin-bom') 
                implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8' 
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/jvmMain/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.output.contains("2 AnimalSniffer violations were found in 1 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference] invalid.(Sample.kt:11)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.kt:16)
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

}
