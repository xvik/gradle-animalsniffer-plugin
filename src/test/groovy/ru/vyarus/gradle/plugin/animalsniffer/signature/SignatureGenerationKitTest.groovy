package ru.vyarus.gradle.plugin.animalsniffer.signature

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import ru.vyarus.gradle.plugin.animalsniffer.signature.support.SignatureReader

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
                compile "org.codehaus.mojo:animal-sniffer-annotations:1.14"
                compile 'junit:junit:4.12'
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
        SignatureReader.readSignature(file("build/animalsniffer/${projectName()}.sig")) == [
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
                files configurations.compile                    
            }

            repositories { mavenCentral()}
            dependencies {
                compile 'junit:junit:4.12'
                compile "org.codehaus.mojo:animal-sniffer-annotations:1.14"
            }

        """
//        debug()

        when: "run task"
        BuildResult result = run('animalsnifferSignature')

        then: "task successful"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.SUCCESS

        then: "validate signature"
        List<String> sigs = SignatureReader.readSignature(file("build/animalsniffer/${projectName()}.sig"))
        sigs.size() > 0
        sigs.contains('org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement')
        sigs.contains('org.junit.Rule')
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
                compile 'junit:junit:4.12'
            }

        """
        fileFromClasspath('src/main/java/valid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('animalsnifferSignature')

        then: "task successful"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.SUCCESS

        then: "validate signature"
        List<String> sigs = SignatureReader.readSignature(file("build/animalsniffer/${projectName()}.sig"))
        sigs.size() > 0
        sigs.contains('valid.Sample')
        sigs.contains('java.lang.Boolean')
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
                compile "org.codehaus.mojo:animal-sniffer-annotations:1.14"
            }
        """
        fileFromClasspath('src/main/java/ann/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/ann/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('animalsnifferSignature')

        then: "task successful"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.SUCCESS

        then: "validate signature"
        SignatureReader.readSignature(file("build/animalsniffer/samplesig.sig")) == [
                'ann.Sample'
        ]
    }
}
