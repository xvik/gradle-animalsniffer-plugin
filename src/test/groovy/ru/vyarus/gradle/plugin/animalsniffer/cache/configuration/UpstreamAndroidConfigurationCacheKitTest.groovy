package ru.vyarus.gradle.plugin.animalsniffer.cache.configuration

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.UpstreamKitTest
import ru.vyarus.gradle.plugin.animalsniffer.android.AbstractAndroidKitTest
import spock.lang.Requires

/**
 * @author Vyacheslav Rusakov
 * @since 12.12.2024
 */
@Requires({jvm.java17Compatible})
class UpstreamAndroidConfigurationCacheKitTest extends AbstractAndroidKitTest {

    def "Check configuration cache compatibility"() {
        setup:
        build """
            plugins {
                id 'com.android.application' version '$UpstreamKitTest.ANDROID_PLUGIN_VERSION'
                id 'org.jetbrains.kotlin.android' version '$UpstreamKitTest.KOTLIN_PLUGIN_VERSION'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
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

        then: "text report correct"
        File file = file('/build/reports/animalsniffer/debug.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:16  Undefined reference (android-api-level-21-5.0.1_r2): java.nio.file.Path java.nio.file.Paths.get(String, String[])",
                "invalid.Sample2:11  Undefined reference (android-api-level-21-5.0.1_r2): java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()",
                "invalid.Sample2:11  Undefined reference (android-api-level-21-5.0.1_r2): Iterable java.nio.file.FileSystem.getFileStores()"
        ]


        when: "run from cache"
        println '\n\n------------------- FROM CACHE ----------------------------------------'
        result = runVer(UpstreamKitTest.GRADLE_VERSION, 'check', '--configuration-cache', '--configuration-cache-problems=warn')

        then: "cache used"
        result.output.contains('Reusing configuration cache.')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferDebug').outcome == TaskOutcome.UP_TO_DATE
        result.task(':animalsnifferRelease').outcome == TaskOutcome.UP_TO_DATE
        // no output!
        !result.output.contains('1 AnimalSniffer violations were found in 1 files.')

        then: "text report correct"
        file.exists()
        file.readLines() == [
                "invalid.Sample:16  Undefined reference (android-api-level-21-5.0.1_r2): java.nio.file.Path java.nio.file.Paths.get(String, String[])",
                "invalid.Sample2:11  Undefined reference (android-api-level-21-5.0.1_r2): java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()",
                "invalid.Sample2:11  Undefined reference (android-api-level-21-5.0.1_r2): Iterable java.nio.file.FileSystem.getFileStores()"
        ]
    }

}
