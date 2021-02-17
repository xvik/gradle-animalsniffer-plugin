package ru.vyarus.gradle.plugin.animalsniffer.cache

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * Demonstrates cache task fail on classes conflict.. impossible to resolve.
 * Normally this test should not pass. It exists to catch animalsniffer version with fixed behaviour.
 *
 * @author Vyacheslav Rusakov
 * @since 11.07.2017
 */
class CacheBuildFailCaseKitTest extends AbstractKitTest {

    def "Check cache build fail"() {
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
                signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
                implementation 'junit:junit:4.12'
            }
        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = runFailed('animalsnifferCacheMain')

        then: "task failed"
        result.task(':animalsnifferCacheMain').outcome == TaskOutcome.FAILED
        result.output.contains('java.lang.ClassCastException: Cannot merge class junit/framework/AssertionFailedError as it has changed superclass')
    }
}
