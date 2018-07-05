package ru.vyarus.gradle.plugin.animalsniffer.signature

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import ru.vyarus.gradle.plugin.animalsniffer.info.SignatureReader

/**
 * @author Vyacheslav Rusakov
 * @since 23.04.2017
 */
class MultipleSourcesGenerationKitTest extends AbstractKitTest {


    def "Check using file multiple sources to build"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsnifferSignature {
                files sourceSets.main.output
                files configurations.compile
            }

            repositories { mavenCentral()}
            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.25'
                compile "org.codehaus.mojo:animal-sniffer-annotations:1.14"
            }
        """
        fileFromClasspath('src/main/java/ann/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/ann/Sample.java')
        fileFromClasspath('src/main/java/valid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('animalsnifferSignature')

        then: "task successful"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.SUCCESS

        then: "validate signature"
        List<String> sigs = SignatureReader.readSignature(file("build/animalsniffer/signature/${projectName()}.sig"))
        sigs.size() > 0
        sigs.contains('ann.Sample')
        sigs.contains('valid.Sample')
        sigs.contains('org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement')
        sigs.contains('org.slf4j.Logger')

        when: "run again"
        result = run('animalsnifferSignature')

        then: "up to date"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.UP_TO_DATE
    }

    def "Check multiple source signatures"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
            
            configurations.create('signature2')
            
            animalsnifferSignature {
                signatures configurations.signature
                signatures configurations.signature2
            }
                        
            repositories { mavenCentral()}
            dependencies {                
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'                
                signature2 'net.sf.androidscents.signature:android-api-level-24:7.0_r2@signature'
            }

        """
//        debug()

        when: "run task"
        BuildResult result = run('animalsnifferSignature')

        then: "task successful"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.SUCCESS

        then: "validate signature"
        List<String> sigs = SignatureReader.readSignature(file("build/animalsniffer/signature/${projectName()}.sig"))
        sigs.size() > 0
        sigs.contains('java.lang.Boolean')
        sigs.contains('android.icu.lang.UProperty')

        when: "run again"
        result = run('animalsnifferSignature')

        then: "up to date"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.UP_TO_DATE
    }
}
