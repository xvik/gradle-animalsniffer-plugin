package ru.vyarus.gradle.plugin.animalsniffer.signature

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import ru.vyarus.gradle.plugin.animalsniffer.info.SignatureReader

/**
 * @author Vyacheslav Rusakov
 * @since 13.04.2017
 */
class SignatureGenerationKitTest extends AbstractKitTest {

    def "Check signature build from classes"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsnifferSignature {
                files sourceSets.main.output 
            }            

            repositories { mavenCentral()}
            dependencies {
                implementation "org.codehaus.mojo:animal-sniffer-annotations:1.14"
                implementation 'org.slf4j:slf4j-api:1.7.25'
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
        SignatureReader.readSignature(file("build/animalsniffer/signature/${projectName()}.sig")) == [
                'ann.Sample',
                'valid.Sample'
        ]
    }


    def "Check signature build from jars"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsnifferSignature {
                files configurations.compileClasspath                    
            }

            repositories { mavenCentral()}
            dependencies {
                implementation 'junit:junit:4.12'
                implementation "org.codehaus.mojo:animal-sniffer-annotations:1.14"
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
        sigs.contains('org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement')
        sigs.contains('org.junit.Rule')

        when: "run again"
        result = run('animalsnifferSignature')

        then: "up to date"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.UP_TO_DATE
    }


    def "Check signature other signatures"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsnifferSignature {
                files sourceSets.main.output
                signatures configurations.signature                    
            }

            repositories { mavenCentral()}
            dependencies {                
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/main/java/valid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('animalsnifferSignature')

        then: "task successful"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.SUCCESS

        then: "validate signature"
        List<String> sigs = SignatureReader.readSignature(file("build/animalsniffer/signature/${projectName()}.sig"))
        sigs.size() > 0
        sigs.contains('valid.Sample')
        sigs.contains('java.lang.Boolean')

        when: "run again"
        result = run('animalsnifferSignature')

        then: "up to date"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.UP_TO_DATE
    }

    def "Check signature name override"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsnifferSignature {
                files sourceSets.main.output 
                outputName 'samplesig'
            }            

            repositories { mavenCentral()}
            dependencies {
                implementation "org.codehaus.mojo:animal-sniffer-annotations:1.14"
            }
        """
        fileFromClasspath('src/main/java/ann/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/ann/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('animalsnifferSignature')

        then: "task successful"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.SUCCESS

        then: "validate signature"
        SignatureReader.readSignature(file("build/animalsniffer/signature/samplesig.sig")) == [
                'ann.Sample'
        ]
        
        when: "run again"
        result = run('animalsnifferSignature')

        then: "up to date"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.UP_TO_DATE
    }
}
