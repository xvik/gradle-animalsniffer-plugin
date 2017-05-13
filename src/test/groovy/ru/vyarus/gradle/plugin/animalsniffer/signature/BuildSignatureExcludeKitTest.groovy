package ru.vyarus.gradle.plugin.animalsniffer.signature

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import ru.vyarus.gradle.plugin.animalsniffer.signature.support.SignatureReader

/**
 * @author Vyacheslav Rusakov
 * @since 24.04.2017
 */
class BuildSignatureExcludeKitTest extends AbstractKitTest {

    def "Check include classes"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsnifferSignature {
                files sourceSets.main.output                
                include 'valid.*' 
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

        then: "only included classes remain in signature"
        SignatureReader.readSignature(file("build/animalsniffer/${projectName()}.sig")) == [
                'valid.Sample'
        ]
    }

    def "Check exclude classes"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsnifferSignature {
                files sourceSets.main.output                
                exclude 'valid.*' 
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

        then: "only included classes remain in signature"
        SignatureReader.readSignature(file("build/animalsniffer/${projectName()}.sig")) == [
                'ann.Sample'
        ]
    }
}
