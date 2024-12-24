package ru.vyarus.gradle.plugin.animalsniffer.android.multiplatform

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.android.AbstractAndroidKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 22.11.2024
 */
@IgnoreIf({ !jvm.java17Compatible })
// android plugin requires java 11
class KotlinMultiplatformAndroidKitTest extends AbstractAndroidKitTest {

    def "Check kotlin multiplatform android support"() {
        setup:
        build """
            import org.jetbrains.kotlin.gradle.dsl.JvmTarget
            
            plugins {
                id 'org.jetbrains.kotlin.multiplatform' version '2.0.21'
                id 'com.android.application' version '8.4.0'                
                id 'ru.vyarus.animalsniffer'
            }
            
            kotlin {
                androidTarget() {
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
        fileFromClasspath('src/commonMain/kotlin/invalid/Sample.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample.kt')
        fileFromClasspath('src/androidMain/kotlin/invalid/Sample2.kt', '/ru/vyarus/gradle/plugin/animalsniffer/kotlin/invalid/Sample2.kt')
        generateManifest('src/androidMain/')
//        debug()

        when: "run task"
        BuildResult result = run('check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferDebug').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferRelease').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
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

        then: "report correct"
        File file = file('/build/reports/animalsniffer/debug.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:11  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:16  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])",
                "invalid.Sample2:10  Undefined reference: java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()",
                "invalid.Sample2:10  Undefined reference: Iterable java.nio.file.FileSystem.getFileStores()"
        ]
    }

}
