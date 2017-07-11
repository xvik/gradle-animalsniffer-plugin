package ru.vyarus.gradle.plugin.animalsniffer.resources

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * Demonstrates resources task fail on classes conflict.. impossible to resolve.
 * Normally this test should not pass. It exists to catch animalsniffer version with fixed behaviour.
 *
 * @author Vyacheslav Rusakov
 * @since 11.07.2017
 */
class ResourceBuildFailCaseKitTest extends AbstractKitTest {

    def "Check resources build fail"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
                useResourcesTask = true
            }

            repositories { mavenCentral() }
            dependencies {
                signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
                compile 'junit:junit:4.12'
            }
        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = runFailed('animalsnifferResourcesMain')

        then: "task failed"
        result.task(':animalsnifferResourcesMain').outcome == TaskOutcome.FAILED
        result.output.contains('java.lang.ClassCastException: Cannot merger class junit/framework/AssertionFailedError as it has changed superclass')
    }
}
