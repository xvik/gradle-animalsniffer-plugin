package ru.vyarus.gradle.plugin.animalsniffer.cache.configuration

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.UpstreamKitTest
import ru.vyarus.gradle.plugin.animalsniffer.debug.PrintAnimalsnifferSourceInfoTask
import ru.vyarus.gradle.plugin.animalsniffer.debug.task.AbstractDebugKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 12.12.2024
 */
@IgnoreIf({ !jvm.java17Compatible })
class UpstreamDebugTaskConfigurationCacheKitTest extends AbstractDebugKitTest {

    def "Check simple java project debug"() {
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
                implementation localGroovy()
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = runVer(UpstreamKitTest.GRADLE_VERSION, PrintAnimalsnifferSourceInfoTask.NAME, '--configuration-cache', '--configuration-cache-problems=warn')

        then: "no configuration cache incompatibilities"
        result.output.contains("1 problem was found storing the configuration cache")
        result.output.contains('Gradle runtime: support for using a Java agent with TestKit')
        result.output.contains('Calculating task graph as no cached configuration is available for tasks:')

        then: "task successful"
        result.task(':' + PrintAnimalsnifferSourceInfoTask.NAME).outcome == TaskOutcome.SUCCESS
        result.output.contains('== [Plugins] =========================')


        when: "run from cache"
        println '\n\n------------------- FROM CACHE ----------------------------------------'
        result = runVer(UpstreamKitTest.GRADLE_VERSION, PrintAnimalsnifferSourceInfoTask.NAME, '--configuration-cache', '--configuration-cache-problems=warn')

        then: "cache used"
        result.output.contains('Reusing configuration cache.')

        then: "task successful"
        result.task(':' + PrintAnimalsnifferSourceInfoTask.NAME).outcome == TaskOutcome.SUCCESS
        result.output.contains('== [Plugins] =========================')
    }

    def "Check java android application debug support"() {
        setup:
        build """
            plugins {
                id 'com.android.application' version '$UpstreamKitTest.ANDROID_PLUGIN_VERSION'
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
            }

            repositories { mavenCentral(); google()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java18:1.0@signature'
                signature 'net.sf.androidscents.signature:android-api-level-21:5.0.1_r2@signature'
                
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """

        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
        generateManifest()
//        debug()

        when: "run task"
        BuildResult result = runVer(UpstreamKitTest.GRADLE_VERSION, PrintAnimalsnifferSourceInfoTask.NAME, '--configuration-cache', '--configuration-cache-problems=warn')

        then: "no configuration cache incompatibilities"
        result.output.contains("1 problem was found storing the configuration cache")
        result.output.contains('Gradle runtime: support for using a Java agent with TestKit')
        result.output.contains('Calculating task graph as no cached configuration is available for tasks:')

        then: "task successful"
        result.task(':' + PrintAnimalsnifferSourceInfoTask.NAME).outcome == TaskOutcome.SUCCESS
        result.output.contains('== [Plugins] =========================')

        when: "run from cache"
        println '\n\n------------------- FROM CACHE ----------------------------------------'
        result = runVer(UpstreamKitTest.GRADLE_VERSION, PrintAnimalsnifferSourceInfoTask.NAME, '--configuration-cache', '--configuration-cache-problems=warn')

        then: "cache used"
        result.output.contains('Reusing configuration cache.')

        then: "task successful"
        result.task(':' + PrintAnimalsnifferSourceInfoTask.NAME).outcome == TaskOutcome.SUCCESS
        result.output.contains('== [Plugins] =========================')

    }

    def "Check kotlin multiplatform jvm support"() {
        setup:
        build """
            plugins {
                id 'org.jetbrains.kotlin.multiplatform' version '$UpstreamKitTest.KOTLIN_PLUGIN_VERSION'
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
        BuildResult result = runVer(UpstreamKitTest.GRADLE_VERSION, PrintAnimalsnifferSourceInfoTask.NAME, '--configuration-cache', '--configuration-cache-problems=warn')

        then: "no configuration cache incompatibilities"
        result.output.contains("1 problem was found storing the configuration cache")
        result.output.contains('Gradle runtime: support for using a Java agent with TestKit')
        result.output.contains('Calculating task graph as no cached configuration is available for tasks:')

        then: "task successful"
        result.task(':' + PrintAnimalsnifferSourceInfoTask.NAME).outcome == TaskOutcome.SUCCESS
        result.output.contains('== [Plugins] =========================')


        when: "run from cache"
        println '\n\n------------------- FROM CACHE ----------------------------------------'
        result = runVer(UpstreamKitTest.GRADLE_VERSION, PrintAnimalsnifferSourceInfoTask.NAME, '--configuration-cache', '--configuration-cache-problems=warn')

        then: "cache used"
        result.output.contains('Reusing configuration cache.')

        then: "task successful"
        result.task(':' + PrintAnimalsnifferSourceInfoTask.NAME).outcome == TaskOutcome.SUCCESS
        result.output.contains('== [Plugins] =========================')
    }
}
