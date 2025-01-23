package ru.vyarus.gradle.plugin.animalsniffer.android.multiplatform

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.android.AbstractAndroidKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 11.12.2024
 */
@IgnoreIf({ !jvm.java17Compatible })
class KotlinMultiplatformMultipleTargetsAndroidKitTest extends AbstractAndroidKitTest {

    def "Check multiple declared platforms (with android)"() {
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
    jvmToolchain(17)
    
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    jvm()
    
    sourceSets {
        commonMain.dependencies {                     
            implementation 'org.slf4j:slf4j-api:1.7.25'
        }
    }  
}

dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
}

android {
    namespace = "org.example.project.shared"
    compileSdk = 34
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = 24
    }
    lint {
        checkReleaseBuilds false
        abortOnError false
    }
}

animalsniffer {
    ignoreFailures = true
}
"""
        fileFromClasspath('shared/src/commonMain/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
        fileFromClasspath('shared/src/jvmMain/kotlin/invalid/Sample2.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample2.kt')


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

animalsniffer {
    ignoreFailures = true
}

dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'

    implementation project(':shared')
    implementation 'ch.qos.logback:logback-classic:1.5.11'
    implementation 'io.ktor:ktor-server-core-jvm:3.0.0'
    implementation 'io.ktor:ktor-server-netty-jvm:3.0.0'
    
    testImplementation 'io.ktor:ktor-server-tests-jvm:2.3.13'
    testImplementation 'org.jetbrains.kotlin:kotlin-test-junit:2.1.0'        
}
"""
        fileFromClasspath('server/src/main/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')


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

animalsniffer {
    ignoreFailures = true
}

kotlin {
    jvmToolchain(17)

    androidTarget {
        compilerOptions {
            jvmTarget == JvmTarget.JVM_17
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
            implementation 'org.slf4j:slf4j-api:1.7.25'
            implementation project(':shared')
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0'
            implementation 'org.slf4j:slf4j-api:1.7.25'
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint {
        quiet true
        checkReleaseBuilds false
        abortOnError false
    }
}

dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'

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
        fileFromClasspath('composeApp/src/commonMain/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
        fileFromClasspath('composeApp/src/desktopMain/kotlin/invalid/Sample2.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample2.kt')
        fileFromClasspath('composeApp/src/androidMain/kotlin/invalid/Sample3.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample3.kt')

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
        BuildResult result = run(':shared:check')

        then: "task successful"
        result.task(':shared:check').outcome == TaskOutcome.SUCCESS
        result.task(':shared:animalsnifferDebug').outcome == TaskOutcome.SUCCESS

        then: "validate shared report"
        result.output.contains("4 AnimalSniffer violations were found in 2 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference] invalid.(Sample.kt:11)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.kt:16)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])

[Undefined reference] invalid.(Sample2.kt:10)
  >> java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()

[Undefined reference] invalid.(Sample2.kt:10)
  >> Iterable java.nio.file.FileSystem.getFileStores()
""")


        when: "run server task"
        result = run(':server:check')

        then: "task successful"
        result.task(':server:check').outcome == TaskOutcome.SUCCESS
        result.task(':server:animalsnifferMain').outcome == TaskOutcome.SUCCESS

        then: "validate server report"
        result.output.contains("2 AnimalSniffer violations were found in 1 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference] invalid.(Sample.kt:11)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.kt:16)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])
""")



        when: "run composeApp task"
        result = run(':composeApp:check')

        then: "task successful"
        result.task(':composeApp:check').outcome == TaskOutcome.SUCCESS
        result.task(':composeApp:animalsnifferDesktopMain').outcome == TaskOutcome.SUCCESS

        then: "validate compose report"
        result.output.contains("4 AnimalSniffer violations were found in 2 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference] invalid.(Sample.kt:11)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.kt:16)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])

[Undefined reference] invalid.(Sample2.kt:10)
  >> java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()

[Undefined reference] invalid.(Sample2.kt:10)
  >> Iterable java.nio.file.FileSystem.getFileStores()
""")
    }
}
