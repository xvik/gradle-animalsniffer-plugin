package ru.vyarus.gradle.plugin.animalsniffer.debug.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 02.12.2024
 */
class JavaModifiedSourcesDebugKitTest extends AbstractDebugKitTest {

    def "Check java modified source sets debug"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
            
            sourceSets {
                main {
                    java {
                        srcDir("src/main2/java")
                    }
                }
                itest {
                    java {
                        srcDir("src/itest/java")
                    }
                }
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
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('printAnimalsnifferSourceInfo')

        then: "task successful"
        result.task(':printAnimalsnifferSourceInfo').outcome == TaskOutcome.SUCCESS

        then: "report validation"
        equalWithDiff(extractReport(result), readReport("repo"))
        !result.output.contains('WARN:')
    }
}
