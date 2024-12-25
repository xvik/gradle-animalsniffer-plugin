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
class MultiplatformAndroidLibSourcesDebugKitTest extends AbstractDebugKitTest {

    def "Check kotlin multiplatform android lib debug"() {
        setup:
        build """
            import org.jetbrains.kotlin.gradle.dsl.JvmTarget
            
            plugins {
                id 'org.jetbrains.kotlin.multiplatform' version '2.0.21'
                id 'com.android.library' version '8.4.0'                
                id 'ru.vyarus.animalsniffer'
            }
            
            kotlin {
                androidTarget {
                    compilerOptions {
                        jvmTarget = JvmTarget.JVM_11
                    }
                }                              
            }
            
            android {
                namespace = "org.example.project"
                compileSdk = 33
            
                defaultConfig {
                    minSdk = 24
                    targetSdk = 24
                    versionCode = 1
                    versionName = "1.0"
                }
                buildTypes {
                    getByName("release") {
                        minifyEnabled = false
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
                lint {
                    quiet true
                    checkReleaseBuilds false
                    abortOnError false
                }
            }
            
            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral(); google()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
 
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }      

        """
        fileFromClasspath('src/androidMain/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
        generateManifest('src/androidMain/')
//        debug()

        when: "run task"
        BuildResult result = run('printAnimalsnifferSourceInfo')

        then: "task successful"
        result.task(':printAnimalsnifferSourceInfo').outcome == TaskOutcome.SUCCESS

        then: "validate report"
        extractReport(result) == readReport("repo")
        !result.output.contains('WARN:')
    }
}