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
class UpstreamAndroidMultiplatformConfigurationCacheKitTest extends AbstractAndroidKitTest {

    def "Check kotlin multiplatform android support"() {
        setup:
        build """
            import org.jetbrains.kotlin.gradle.dsl.JvmTarget
            
            plugins {
                id 'org.jetbrains.kotlin.multiplatform' version '$UpstreamKitTest.KOTLIN_PLUGIN_VERSION'
                id 'com.android.application' version '$UpstreamKitTest.ANDROID_PLUGIN_VERSION'                
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
                    applicationId = "org.example.project"
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
        BuildResult result = runVer(UpstreamKitTest.GRADLE_VERSION, 'check', '--configuration-cache', '--configuration-cache-problems=warn')

        then: "no configuration cache incompatibilities"
        result.output.contains("1 problem was found storing the configuration cache")
        result.output.contains('Gradle runtime: support for using a Java agent with TestKit')
        result.output.contains('Calculating task graph as no cached configuration is available for tasks:')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
        result.output.contains('2 AnimalSniffer violations were found in 1 files.')

        then: "text report correct"
        File file = file('/build/reports/animalsniffer/debug.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:11  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:16  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]

        when: "run from cache"
        println '\n\n------------------- FROM CACHE ----------------------------------------'
        result = runVer(UpstreamKitTest.GRADLE_VERSION, 'check', '--configuration-cache', '--configuration-cache-problems=warn')

        then: "cache used"
        result.output.contains('Reusing configuration cache.')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
        // no output!
        !result.output.contains('2 AnimalSniffer violations were found in 1 files.')

        then: "text report correct"
        File file2 = super.file('/build/reports/animalsniffer/debug.text')
        file2.exists()
        file2.readLines() == [
                "invalid.Sample:11  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:16  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }
}
