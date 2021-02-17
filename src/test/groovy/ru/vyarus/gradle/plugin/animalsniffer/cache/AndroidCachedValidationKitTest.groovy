package ru.vyarus.gradle.plugin.animalsniffer.cache

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 14.07.2017
 */
class AndroidCachedValidationKitTest extends AbstractKitTest {

    def "Check multiple signatures with cache"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
                cache.enabled = true
            }

            repositories { mavenCentral() }
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }
        """
        fileFromClasspath('src/main/java/android/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/android/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.output.contains("3 AnimalSniffer violations were found in 1 files")
        result.output.contains("[Undefined reference | java16-sun-1.0]")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() as Set == [
                'android.Sample:9  Undefined reference (android-api-level-14-4.0_r4): int Boolean.compare(boolean, boolean)',
                'android.Sample:14  Undefined reference (android-api-level-14-4.0_r4): Object javax.naming.InitialContext.doLookup(String)',
                'android.Sample:9  Undefined reference (java16-sun-1.0): int Boolean.compare(boolean, boolean)'
        ] as Set
    }
}
