package ru.vyarus.gradle.plugin.animalsniffer.debug.task.multiplatform

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.debug.task.AbstractDebugKitTest
import spock.lang.IgnoreIf

/**
 * Based on project from https://kmp.jetbrains.com/
 * Sample app with desktop and server targets (multi-module)
 *
 * @author Vyacheslav Rusakov
 * @since 02.12.2024
 */
@IgnoreIf({ !jvm.java17Compatible })
class MultiplatformMultiplePlatformsSourcesDebugKitTest extends AbstractDebugKitTest {


    def "Check multiple declared platforms debug (no android)"() {
        setup:
        build """
            // this is necessary to avoid the plugins to be loaded multiple times
            // in each subproject's classloader            
            plugins {
                id 'org.jetbrains.compose' version '1.7.0' apply false
                id 'org.jetbrains.kotlin.plugin.compose' version '2.0.21' apply false
                id 'org.jetbrains.kotlin.jvm' version '2.0.21' apply false
                id 'org.jetbrains.kotlin.multiplatform' version '2.0.21' apply false                
                id 'ru.vyarus.animalsniffer' apply false
            }            
        """

        file('shared/build.gradle') << """
plugins {
    id 'org.jetbrains.kotlin.multiplatform'
    id 'ru.vyarus.animalsniffer'
}

kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
    }
}
"""
        file('server/build.gradle') << """
plugins {
    id 'org.jetbrains.kotlin.jvm'
    id 'io.ktor.plugin' version '3.0.0'  
    id 'application'
    id 'ru.vyarus.animalsniffer'
}

group = "org.example.project"
version = "1.0.0"

application {
    mainClass = "org.example.project.ApplicationKt"
    applicationDefaultJvmArgs = ["-Dio.ktor.development=true"]
}

dependencies {
    implementation project(':shared')
    implementation 'ch.qos.logback:logback-classic:1.5.11'
    implementation 'io.ktor:ktor-server-core-jvm:3.0.0'
    implementation 'io.ktor:ktor-server-netty-jvm:3.0.0'
    
    testImplementation 'io.ktor:ktor-server-tests-jvm:2.3.13'
    testImplementation 'org.jetbrains.kotlin:kotlin-test-junit:2.1.0'        
}
"""

        file('composeApp/build.gradle') << """    
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id 'org.jetbrains.kotlin.multiplatform'
    id 'org.jetbrains.compose'
    id 'org.jetbrains.kotlin.plugin.compose'
    id 'ru.vyarus.animalsniffer'
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation 'org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:2.8.3'
            implementation 'org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.8.3'
            implementation project(':shared')
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0'
        }
    }
}


compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}
"""

        file("settings.gradle") << """            
            include ':composeApp', ':server', ':shared'
            """

//        fileFromClasspath('src/androidMain/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
//        debug()

        when: "run shared task"
        BuildResult result = run(':shared:printAnimalsnifferSourceInfo')

        then: "task successful"
        result.task(':shared:printAnimalsnifferSourceInfo').outcome == TaskOutcome.SUCCESS

        then: "validate shared report"
        equalWithDiff(extractReport(result), readReport("shared"))
        !result.output.contains('WARN:')


        when: "run server task"
        result = run(':server:printAnimalsnifferSourceInfo')

        then: "task successful"
        result.task(':server:printAnimalsnifferSourceInfo').outcome == TaskOutcome.SUCCESS

        then: "validate server report"
        equalWithDiff(extractReport(result), readReport("server"))
        !result.output.contains('WARN:')


        when: "run composeApp task"
        result = run(':composeApp:printAnimalsnifferSourceInfo')

        then: "task successful"
        result.task(':composeApp:printAnimalsnifferSourceInfo').outcome == TaskOutcome.SUCCESS

        then: "validate compose report"
        equalWithDiff(extractReport(result)
                .replace('skiko-awt-runtime-windows', 'skiko-awt-runtime-linux'), readReport("composeApp"))
        !result.output.contains('WARN:')
    }

}


