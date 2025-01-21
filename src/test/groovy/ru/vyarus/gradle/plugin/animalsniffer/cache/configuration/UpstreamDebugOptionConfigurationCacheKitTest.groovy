package ru.vyarus.gradle.plugin.animalsniffer.cache.configuration

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.UpstreamKitTest
import ru.vyarus.gradle.plugin.animalsniffer.android.AbstractAndroidKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 12.12.2024
 */
@IgnoreIf({ !jvm.java17Compatible })
class UpstreamDebugOptionConfigurationCacheKitTest extends AbstractAndroidKitTest {

    def "Check configuration cache support"() {

        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
            
            animalsniffer {
                debug = true
                ignoreFailures = true                
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }
        """

        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
        //debug()

        when: "run task"
        BuildResult result = runVer(UpstreamKitTest.GRADLE_VERSION, 'check', '--configuration-cache', '--configuration-cache-problems=warn')

        then: "no configuration cache incompatibilities"
        result.output.contains("1 problem was found storing the configuration cache")
        result.output.contains('Gradle runtime: support for using a Java agent with TestKit')
        result.output.contains('Calculating task graph as no cached configuration is available for tasks:')

        then: "task successful"
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS
        result.output.contains('2 AnimalSniffer violations were found in 1 files')
        unifyStringLinSlashes(result.output).contains('\tsignatures:\n' +
                '\t\tjava16-sun-1.0.signature\n' +
                '\n' +
                '\tsources:\n' +
                '\t\tsrc/main/java')

        when: "run from cache"
        println '\n\n------------------- FROM CACHE ----------------------------------------'
        result = runVer(UpstreamKitTest.GRADLE_VERSION, 'check', '--configuration-cache', '--configuration-cache-problems=warn')

        then: "cache used"
        result.output.contains('Reusing configuration cache.')

        then: "task successful"
        result.task(':animalsnifferMain').outcome == TaskOutcome.UP_TO_DATE
        // no output
        !result.output.contains('2 AnimalSniffer violations were found in 1 files')
    }

    def "Check configuration cache compatibility"() {
        setup:
        build """
            plugins {
                id 'com.android.application' version '$UpstreamKitTest.ANDROID_PLUGIN_VERSION'
                id 'org.jetbrains.kotlin.android' version '$UpstreamKitTest.KOTLIN_PLUGIN_VERSION'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                debug = true
                ignoreFailures = true
            }
            
            android {
                compileSdk 33
                namespace 'com.example.namespace'
                def javaVersion = JavaVersion.VERSION_17
            
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

        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
        fileFromClasspath('src/debug/java/invalid/Sample2.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample2.java')
        generateManifest()

//        debug()

        when: "run task"
        BuildResult result = runVer(UpstreamKitTest.GRADLE_VERSION, 'check', '--configuration-cache', '--configuration-cache-problems=warn')

        then: "no configuration cache incompatibilities"
        result.output.contains("1 problem was found storing the configuration cache")
        result.output.contains('Gradle runtime: support for using a Java agent with TestKit')
        result.output.contains('Calculating task graph as no cached configuration is available for tasks:')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferDebug').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferRelease').outcome == TaskOutcome.SUCCESS
        result.output.contains('3 AnimalSniffer violations were found in 2 files')
        unifyStringLinSlashes(result.output).contains('signatures:\n' +
                '\t\tjava18-1.0.signature\n' +
                '\t\tandroid-api-level-21-5.0.1_r2.signature\n' +
                '\n' +
                '\tsources:\n' +
                '\t\tsrc/debug/java\n' +
                '\t\tsrc/debug/kotlin\n' +
                '\t\tsrc/main/java\n' +
                '\t\tsrc/main/kotlin')

        when: "run from cache"
        println '\n\n------------------- FROM CACHE ----------------------------------------'
        result = runVer(UpstreamKitTest.GRADLE_VERSION, 'check', '--configuration-cache', '--configuration-cache-problems=warn')

        then: "cache used"
        result.output.contains('Reusing configuration cache.')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferDebug').outcome == TaskOutcome.UP_TO_DATE
        result.task(':animalsnifferRelease').outcome == TaskOutcome.UP_TO_DATE
        // no output
        !result.output.contains('3 AnimalSniffer violations were found in 2 files')
    }
}
