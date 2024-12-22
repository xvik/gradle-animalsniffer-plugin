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
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "tasks alisted"
        def out = clean(result.output)
        out.contains("""Registered animalsniffer tasks:

\tanimalsnifferDebug                  [default]       for 'debug' android variant
\t\treport: build/reports/animalsniffer/debug.text
\t\tdepends on: debugAnimalSnifferClassesCollector
\t\tsignatures: 
\t\t\tandroid-api-level-21-5.0.1_r2.signature
\t\t\tjava18-1.0.signature
\t\tclasses:
\t\t\tbuild/intermediates/animal_sniffer/debugAnimalSnifferClassesCollector

\tanimalsnifferRelease                [default]       for 'release' android variant
\t\treport: build/reports/animalsniffer/release.text
\t\tdepends on: releaseAnimalSnifferClassesCollector
\t\tsignatures: 
\t\t\tandroid-api-level-21-5.0.1_r2.signature
\t\t\tjava18-1.0.signature
\t\tclasses:
\t\t\tbuild/intermediates/animal_sniffer/releaseAnimalSnifferClassesCollector
""")

        then: "debug validation"
        out.contains """
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
\t\tbuild/intermediates/animal_sniffer/debugAnimalSnifferClassesCollector/com/example/namespace/BuildConfig.class
\t\tbuild/intermediates/animal_sniffer/debugAnimalSnifferClassesCollector/invalid/Sample.class
"""
    }

    private clean(String out) {
        return out.replace('\r', '').replace(File.separator, '/')
    }
}
