package ru.vyarus.gradle.plugin.animalsniffer.debug

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov
 * @since 12.12.2024
 */
@IgnoreIf({ !jvm.java17Compatible })
class DebugMultiplatformKitTest extends AbstractKitTest {

    def "Check kotlin multiplatform jvm support"() {
        setup:
        build """
            plugins {
                id 'org.jetbrains.kotlin.multiplatform' version '2.0.21'
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
                debug = true
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
        BuildResult result = run('printAnimalsnifferTasks', 'check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        then: "tasks list shown"
        def out = clean(result.output)
        out.contains(""":printAnimalsnifferTasks


\tanimalsnifferJvmMain                [default]       for kotlin platform 'jvm' compilation 'main'
\t\treport: build/reports/animalsniffer/jvmMain.text
\t\tdepends on: jvmMainClasses
\t\tsignatures: 
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/kotlin/jvm/main
\t\tsources:
\t\t\tsrc/commonMain/kotlin                                                            NOT EXISTS
\t\t\tsrc/jvmMain/kotlin


\tanimalsnifferJvmTest                                for kotlin platform 'jvm' compilation 'test'
\t\treport: build/reports/animalsniffer/jvmTest.text
\t\tdepends on: jvmTestClasses
\t\tsignatures: 
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/kotlin/jvm/test
\t\tsources:
\t\t\tsrc/commonTest/kotlin                                                            NOT EXISTS
\t\t\tsrc/jvmTest/kotlin                                                               NOT EXISTS

*use [debugAnimalsnifferSources] task to see project sources configuration details
""")

        then: "debug validation"
        out.contains """
\tsignatures:
\t\tjava16-sun-1.0.signature

\tsources:
\t\tsrc/commonMain/kotlin
\t\tsrc/jvmMain/kotlin

\tfiles:
\t\tbuild/classes/kotlin/jvm/main/META-INF/testProjectDir11111111.kotlin_module
\t\tbuild/classes/kotlin/jvm/main/invalid/Sample.class
"""
    }


    private clean(String out) {
        return out.replace('\r', '').replace(File.separator, '/')
        .replace(testProjectDir.name, 'testProjectDir11111111')
    }
}
