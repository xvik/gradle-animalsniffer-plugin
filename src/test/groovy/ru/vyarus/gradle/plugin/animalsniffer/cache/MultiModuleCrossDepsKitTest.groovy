package ru.vyarus.gradle.plugin.animalsniffer.cache

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 21.05.2017
 */
class MultiModuleCrossDepsKitTest extends AbstractKitTest {

    def "Check module dependencies"() {
        setup:
        build("""
            plugins {
                id 'ru.vyarus.animalsniffer'
            }

            subprojects {
                apply plugin: 'java'
                apply plugin: 'ru.vyarus.animalsniffer'

                animalsniffer {
                    ignoreFailures = true
                    cache.enabled = true
                }

                repositories { mavenCentral()}
                dependencies {
                    signature 'org.codehaus.mojo.signature:java16:1.0@signature'
                    compile 'org.slf4j:slf4j-api:1.7.25'
                }
            }
            
            project(':mod2') {
                dependencies {                    
                    compile project(':mod1')
                }
            }
            
            project(':mod3') {
                dependencies {                    
                    compile project(':mod2')
                }
            }
        """)

        file('settings.gradle') << ' include "mod1", "mod2", "mod3"'
        fileFromClasspath("mod1/src/main/java/valid/Sample.java", '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
        fileFromClasspath("mod2/src/main/java/valid/Sample.java", '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
        fileFromClasspath("mod3/src/main/java/valid/Sample.java", '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
//        debug()

        when: "run check for all modules"
        BuildResult result = run('animalsnifferMain')

        then: "all tasks executed"
        result.task(':mod1:animalsnifferCacheMain').outcome == TaskOutcome.SUCCESS
        result.task(':mod1:animalsnifferMain').outcome == TaskOutcome.SUCCESS
        result.task(':mod2:animalsnifferCacheMain').outcome == TaskOutcome.SUCCESS
        result.task(':mod2:animalsnifferMain').outcome == TaskOutcome.SUCCESS
        result.task(':mod3:animalsnifferCacheMain').outcome == TaskOutcome.SUCCESS
        result.task(':mod3:animalsnifferMain').outcome == TaskOutcome.SUCCESS

        when: "mod1 modified"
        fileFromClasspath("mod1/src/main/java/valid/Sample.java", '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample_1.java')
        result = run('animalsnifferMain', '-i')

        then: "signatures are not re-generated"
        result.task(':mod1:animalsnifferCacheMain').outcome == TaskOutcome.UP_TO_DATE
        result.task(':mod1:animalsnifferMain').outcome == TaskOutcome.SUCCESS
        result.task(':mod2:animalsnifferCacheMain').outcome == TaskOutcome.UP_TO_DATE
        result.task(':mod2:animalsnifferMain').outcome == TaskOutcome.SUCCESS
        result.task(':mod3:animalsnifferCacheMain').outcome == TaskOutcome.UP_TO_DATE
        result.task(':mod3:animalsnifferMain').outcome == TaskOutcome.SUCCESS
    }
}
