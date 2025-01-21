package ru.vyarus.gradle.plugin.animalsniffer.debug.task.multiplatform

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.debug.task.AbstractDebugKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 02.12.2024
 */
@IgnoreIf({ !jvm.java17Compatible })
class MultiplatformDebugKitTest extends AbstractDebugKitTest {

    def "Check kotlin multiplatform jvm support"() {
        setup:
        build """
            plugins {
                id 'org.jetbrains.kotlin.multiplatform' version '2.0.21'
                id 'ru.vyarus.animalsniffer'
            }
            
            kotlin {
                jvm()
                
                sourceSets {
                    commonMain.dependencies {
                        implementation 'org.slf4j:slf4j-api:1.7.25'
                    }                    
                }
            }
            
            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'                
            }                 
        """
        fileFromClasspath('src/jvmMain/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
//        debug()

        when: "run task"
        BuildResult result = run('printAnimalsnifferSourceInfo')

        then: "task successful"
        result.task(':printAnimalsnifferSourceInfo').outcome == TaskOutcome.SUCCESS

        then: "validate report"
        equalWithDiff(extractReport(result), readReport("nojava"))
        !result.output.contains('WARN:')
    }


    def "Check kotlin multiplatform jvm with java"() {
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
        BuildResult result = run('printAnimalsnifferSourceInfo')

        then: "task successful"
        result.task(':printAnimalsnifferSourceInfo').outcome == TaskOutcome.SUCCESS

        then: "validate report"
        equalWithDiff(extractReport(result), readReport("java"))
        !result.output.contains('WARN:')
    }

}
