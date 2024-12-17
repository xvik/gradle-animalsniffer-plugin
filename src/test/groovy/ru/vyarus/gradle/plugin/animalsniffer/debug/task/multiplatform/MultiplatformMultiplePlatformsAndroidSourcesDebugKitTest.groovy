package ru.vyarus.gradle.plugin.animalsniffer.debug.task.multiplatform

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.debug.task.AbstractDebugKitTest
import spock.lang.IgnoreIf

/**
 * Based on project from https://kmp.jetbrains.com/
 * Sample app with desktop, server and android targets (multi-module)
 *
 * @author Vyacheslav Rusakov
 * @since 02.12.2024
 */
@IgnoreIf({ !jvm.java17Compatible })
class MultiplatformMultiplePlatformsAndroidSourcesDebugKitTest extends AbstractDebugKitTest {

    def "Check multiple declared platforms debug (with android)"() {
        setup:
        build """
            // this is necessary to avoid the plugins to be loaded multiple times
            // in each subproject's classloader            
            plugins {
                id 'com.android.application' version '8.4.0' apply false
                id 'com.android.library' version '8.4.0' apply false
                id 'org.jetbrains.compose' version '1.7.0' apply false
                id 'org.jetbrains.kotlin.plugin.compose' version '2.0.21' apply false
                id 'org.jetbrains.kotlin.jvm' version '2.0.21' apply false
                id 'org.jetbrains.kotlin.multiplatform' version '2.0.21' apply false                
                id 'ru.vyarus.animalsniffer' apply false
            }            
        """

        file('shared/build.gradle') << """      
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id 'org.jetbrains.kotlin.multiplatform'
    id 'com.android.library'
    id 'ru.vyarus.animalsniffer'
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    jvm()
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
    }
}

android {
    namespace = "org.example.project.shared"
    compileSdk = 34
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = 24
    }
    lint {
        checkReleaseBuilds false
        abortOnError false
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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id 'org.jetbrains.kotlin.multiplatform'
    id 'com.android.application'
    id 'org.jetbrains.compose'
    id 'org.jetbrains.kotlin.plugin.compose'
    id 'ru.vyarus.animalsniffer'
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget == JvmTarget.JVM_11
        }
    }

    jvm("desktop")
    
    sourceSets {
    
        androidMain.dependencies {
            implementation(compose.preview)
            implementation 'androidx.activity:activity-compose:1.9.3'
        }
        
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

android {
    namespace = "org.example.project"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = 24
        targetSdk = 34
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

dependencies {
    debugImplementation(compose.uiTooling)
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
        generateManifest('composeApp/src/androidMain')
        file("settings.gradle") << """            
            include ':composeApp', ':server', ':shared'
            """
        file("gradle.properties") << """ 
android.nonTransitiveRClass=true
android.useAndroidX=true
io.ktor.development=true
"""

//        fileFromClasspath('src/androidMain/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
//        debug()

        when: "run shared task"
        BuildResult result = run(':shared:debugAnimalsnifferSources')

        then: "task successful"
        result.task(':shared:debugAnimalsnifferSources').outcome == TaskOutcome.SUCCESS

        then: "validate shared report"

        def report = extractReport(result)
        report == readReport('shared')
        !result.output.contains('WARN:')


        when: "run server task"
        result = run(':server:debugAnimalsnifferSources')

        then: "task successful"
        result.task(':server:debugAnimalsnifferSources').outcome == TaskOutcome.SUCCESS

        then: "validate server report"
        extractReport(result) == readReport('server')
        !result.output.contains('WARN:')


        when: "run composeApp task"
        result = run(':composeApp:debugAnimalsnifferSources')

        then: "task successful"
        result.task(':composeApp:debugAnimalsnifferSources').outcome == TaskOutcome.SUCCESS

        then: "validate compose report"
        extractReport(result) == readReport('composeApp')
        !result.output.contains('WARN:')
    }
}
