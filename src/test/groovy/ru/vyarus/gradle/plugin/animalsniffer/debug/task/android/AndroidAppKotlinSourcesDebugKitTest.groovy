package ru.vyarus.gradle.plugin.animalsniffer.debug.task.android

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.debug.task.AbstractDebugKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 02.12.2024
 */
@IgnoreIf({ !jvm.java11Compatible })
class AndroidAppKotlinSourcesDebugKitTest extends AbstractDebugKitTest {

    def "Check kotlin android application debug support"() {
        setup:
        build """
            plugins {
                id 'com.android.application' version '7.4.0'
                id 'org.jetbrains.kotlin.android' version '1.7.22'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }
            
            android {
                compileSdk 33
                namespace 'com.example.namespace'
                def javaVersion = JavaVersion.VERSION_1_8
            
                defaultConfig {
                    minSdkVersion 21
                }
            
                lint {
                    checkReleaseBuilds false
                    abortOnError false
                }
            
                compileOptions {
                    sourceCompatibility(javaVersion)
                    targetCompatibility(javaVersion)
                }
                
                kotlinOptions {
                    jvmTarget = javaVersion.toString()
                }
            }

            repositories { mavenCentral(); google()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java18:1.0@signature'
                signature 'net.sf.androidscents.signature:android-api-level-21:5.0.1_r2@signature'
                
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """

        fileFromClasspath('src/main/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
        generateManifest()
//        debug()

        when: "run task"
        BuildResult result = run('debugAnimalsnifferSources')

        then: "task successful"
        result.task(':debugAnimalsnifferSources').outcome == TaskOutcome.SUCCESS

        then: "report validation"
        extractReport(result) == readReport("repo")
        !result.output.contains('WARN:')
    }
}
