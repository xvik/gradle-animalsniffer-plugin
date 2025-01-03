package ru.vyarus.gradle.plugin.animalsniffer.debug

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.android.AbstractAndroidKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 12.12.2024
 */
@IgnoreIf({ !jvm.java11Compatible })
class DebugAndroidKitTest extends AbstractAndroidKitTest {

    def "Check java android application debug support"() {
        setup:
        build """
            plugins {
                id 'com.android.application' version '7.4.0'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                debug = true
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
        BuildResult result = run('printAnimalsnifferTasks', 'check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "tasks alisted"
        def out = clean(result.output)
        out.contains(""":printAnimalsnifferTasks


\tanimalsnifferDebug                  [default]       for 'debug' android variant
\t\treport: build/reports/animalsniffer/debug.text
\t\tdepends on: debugAnimalsnifferClassesCollector
\t\tsignatures: 
\t\t\tandroid-api-level-21-5.0.1_r2.signature
\t\t\tjava18-1.0.signature
\t\tclasses:
\t\t\tbuild/intermediates/javac/debug/classes
\t\tsources:
\t\t\tbuild/generated/aidl_source_output_dir/debug/out                                 NOT EXISTS
\t\t\tbuild/generated/renderscript_source_output_dir/debug/out                         NOT EXISTS
\t\t\tbuild/generated/source/buildConfig/debug
\t\t\tsrc/debug/java                                                                   NOT EXISTS
\t\t\tsrc/debug/kotlin                                                                 NOT EXISTS
\t\t\tsrc/main/java
\t\t\tsrc/main/kotlin                                                                  NOT EXISTS


\tanimalsnifferDebugAndroidTest                       for 'debugAndroidTest' android test component
\t\treport: build/reports/animalsniffer/debugAndroidTest.text
\t\tdepends on: debugAndroidTestAnimalsnifferClassesCollector
\t\tsignatures: 
\t\t\tandroid-api-level-21-5.0.1_r2.signature
\t\t\tjava18-1.0.signature
\t\tclasses:
\t\t\tbuild/intermediates/javac/debugAndroidTest/classes
\t\tsources:
\t\t\tbuild/generated/aidl_source_output_dir/debugAndroidTest/out                      NOT EXISTS
\t\t\tbuild/generated/renderscript_source_output_dir/debugAndroidTest/out              NOT EXISTS
\t\t\tbuild/generated/source/buildConfig/androidTest/debug
\t\t\tsrc/androidTest/java                                                             NOT EXISTS
\t\t\tsrc/androidTest/kotlin                                                           NOT EXISTS
\t\t\tsrc/androidTestDebug/java                                                        NOT EXISTS
\t\t\tsrc/androidTestDebug/kotlin                                                      NOT EXISTS


\tanimalsnifferDebugUnitTest                          for 'debugUnitTest' android test component
\t\treport: build/reports/animalsniffer/debugUnitTest.text
\t\tdepends on: debugUnitTestAnimalsnifferClassesCollector
\t\tsignatures: 
\t\t\tandroid-api-level-21-5.0.1_r2.signature
\t\t\tjava18-1.0.signature
\t\tclasses:
\t\t\t<empty>
\t\tsources:
\t\t\tsrc/test/java                                                                    NOT EXISTS
\t\t\tsrc/test/kotlin                                                                  NOT EXISTS
\t\t\tsrc/testDebug/java                                                               NOT EXISTS
\t\t\tsrc/testDebug/kotlin                                                             NOT EXISTS


\tanimalsnifferRelease                [default]       for 'release' android variant
\t\treport: build/reports/animalsniffer/release.text
\t\tdepends on: releaseAnimalsnifferClassesCollector
\t\tsignatures: 
\t\t\tandroid-api-level-21-5.0.1_r2.signature
\t\t\tjava18-1.0.signature
\t\tclasses:
\t\t\tbuild/intermediates/javac/release/classes
\t\tsources:
\t\t\tbuild/generated/aidl_source_output_dir/release/out                               NOT EXISTS
\t\t\tbuild/generated/renderscript_source_output_dir/release/out                       NOT EXISTS
\t\t\tbuild/generated/source/buildConfig/release
\t\t\tsrc/main/java
\t\t\tsrc/main/kotlin                                                                  NOT EXISTS
\t\t\tsrc/release/java                                                                 NOT EXISTS
\t\t\tsrc/release/kotlin                                                               NOT EXISTS


\tanimalsnifferReleaseUnitTest                        for 'releaseUnitTest' android test component
\t\treport: build/reports/animalsniffer/releaseUnitTest.text
\t\tdepends on: releaseUnitTestAnimalsnifferClassesCollector
\t\tsignatures: 
\t\t\tandroid-api-level-21-5.0.1_r2.signature
\t\t\tjava18-1.0.signature
\t\tclasses:
\t\t\t<empty>
\t\tsources:
\t\t\tsrc/test/java                                                                    NOT EXISTS
\t\t\tsrc/test/kotlin                                                                  NOT EXISTS
\t\t\tsrc/testRelease/java                                                             NOT EXISTS
\t\t\tsrc/testRelease/kotlin                                                           NOT EXISTS

*use [printAnimalsnifferSourceInfo] task to see project sources configuration details
""")

        then: "debug validation"
        out.contains """:animalsnifferDebug

\tsignatures:
\t\tjava18-1.0.signature
\t\tandroid-api-level-21-5.0.1_r2.signature

\tsources:
\t\tbuild/generated/aidl_source_output_dir/debug/out
\t\tbuild/generated/renderscript_source_output_dir/debug/out
\t\tbuild/generated/source/buildConfig/debug
\t\tsrc/debug/java
\t\tsrc/debug/kotlin
\t\tsrc/main/java
\t\tsrc/main/kotlin

\tfiles:
\t\tbuild/intermediates/javac/debug/classes/com/example/namespace/BuildConfig.class
\t\tbuild/intermediates/javac/debug/classes/invalid/Sample.class
"""


        then: "release validation"
        out.contains """:animalsnifferRelease

\tsignatures:
\t\tjava18-1.0.signature
\t\tandroid-api-level-21-5.0.1_r2.signature

\tsources:
\t\tbuild/generated/aidl_source_output_dir/release/out
\t\tbuild/generated/renderscript_source_output_dir/release/out
\t\tbuild/generated/source/buildConfig/release
\t\tsrc/main/java
\t\tsrc/main/kotlin
\t\tsrc/release/java
\t\tsrc/release/kotlin

\tfiles:
\t\tbuild/intermediates/javac/release/classes/com/example/namespace/BuildConfig.class
\t\tbuild/intermediates/javac/release/classes/invalid/Sample.class
"""
    }

    private clean(String out) {
        return out.replace('\r', '').replace(File.separator, '/')
    }
}
