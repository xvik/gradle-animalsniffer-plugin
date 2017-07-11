package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * Report is not created when no violations detected, so successful animalsnfferMain would be
 * up-to-date after clean.
 * Proper check of up-to-date state could be performed only on task producing violations
 *
 * @author Vyacheslav Rusakov
 * @since 15.05.2017
 */
class UpToDateKitTest extends AbstractKitTest {

    def "Check correct behaviour on second launch"() {

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
                compile 'org.slf4j:slf4j-api:1.7.25'
            }
        """

        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
        //debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':animalsnifferResourcesMain').outcome == TaskOutcome.SKIPPED
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS

        when: "run one more time"
        result = run('check')

        then: "up to date"
        result.task(':animalsnifferResourcesMain').outcome == TaskOutcome.SKIPPED
        result.task(':animalsnifferMain').outcome == TaskOutcome.UP_TO_DATE

        when: "run again after clean"
        result = run('clean', 'check')

        then: "executed"
        result.task(':animalsnifferResourcesMain').outcome == TaskOutcome.SKIPPED
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS
    }
}