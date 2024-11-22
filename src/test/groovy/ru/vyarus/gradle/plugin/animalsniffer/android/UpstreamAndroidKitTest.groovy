package ru.vyarus.gradle.plugin.animalsniffer.android

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.UpstreamKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 22.11.2024
 */
@IgnoreIf({ !jvm.java17Compatible })
class UpstreamAndroidKitTest extends AbstractAndroidKitTest {

    def "Check android library support"() {
        setup:
        build """
            plugins {
                id 'com.android.library' version '8.7.2'
                id 'org.jetbrains.kotlin.android' version '2.0.21'
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
//        debug()

        when: "run task"
        BuildResult result = runVer(UpstreamKitTest.GRADLE_VERSION, 'check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferDebug').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferRelease') == null

        then: "found 1 violation"
        result.output.contains("3 AnimalSniffer violations were found in 2 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference | android-api-level-21-5.0.1_r2] invalid.(Sample.java:16)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])

[Undefined reference | android-api-level-21-5.0.1_r2] invalid.(Sample2.java:11)
  >> java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()

[Undefined reference | android-api-level-21-5.0.1_r2] invalid.(Sample2.java:11)
  >> Iterable java.nio.file.FileSystem.getFileStores()
""")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/debug.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:16  Undefined reference (android-api-level-21-5.0.1_r2): java.nio.file.Path java.nio.file.Paths.get(String, String[])",
                "invalid.Sample2:11  Undefined reference (android-api-level-21-5.0.1_r2): java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()",
                "invalid.Sample2:11  Undefined reference (android-api-level-21-5.0.1_r2): Iterable java.nio.file.FileSystem.getFileStores()"
        ]
    }

    def "Check android application support"() {
        setup:
        build """
            plugins {
                id 'com.android.application' version '8.7.2'
                id 'org.jetbrains.kotlin.android' version '2.0.21'
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
        BuildResult result = runVer(UpstreamKitTest.GRADLE_VERSION, 'check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferDebug').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferRelease') == null

        then: "found 1 violation"
        result.output.contains("3 AnimalSniffer violations were found in 2 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference | android-api-level-21-5.0.1_r2] invalid.(Sample.java:16)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])

[Undefined reference | android-api-level-21-5.0.1_r2] invalid.(Sample2.java:11)
  >> java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()

[Undefined reference | android-api-level-21-5.0.1_r2] invalid.(Sample2.java:11)
  >> Iterable java.nio.file.FileSystem.getFileStores()
""")

        then: "report correct"
        File file = file('/build/reports/animalsniffer/debug.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:16  Undefined reference (android-api-level-21-5.0.1_r2): java.nio.file.Path java.nio.file.Paths.get(String, String[])",
                "invalid.Sample2:11  Undefined reference (android-api-level-21-5.0.1_r2): java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()",
                "invalid.Sample2:11  Undefined reference (android-api-level-21-5.0.1_r2): Iterable java.nio.file.FileSystem.getFileStores()"
        ]
    }
}
