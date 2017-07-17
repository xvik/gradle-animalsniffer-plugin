package ru.vyarus.gradle.plugin.animalsniffer.info

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 14.05.2017
 */
class SignatureInfoKitTest extends AbstractKitTest {

    def "Check java signature print"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
            }
            
            task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
                signature = configurations.signature
                depth = 2
            } 

        """
//        debug()

        when: "run task"
        BuildResult result = run('printSignature')

        then: "task successful"
        result.task(':printSignature').outcome == TaskOutcome.SUCCESS
        result.output.contains("Signature java16-sun-1.0.signature (1.7 Mb) contains 18312 classes")
        result.output.contains("com.sun                        7115")
    }

    def "Check signature of cache task output"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
            
            animalsniffer {
                cache.enabled = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                compile 'junit:junit:4.12'
            }
            
            task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
                signature = animalsnifferCacheMain.outputs.files
                depth = 2
            } 

        """
//        debug()

        when: "run task"
        BuildResult result = run('printSignature')

        then: "task successful"
        result.task(':printSignature').outcome == TaskOutcome.SUCCESS
        result.output.contains("Signature animalsnifferCacheMain.sig (1.3 Mb) contains 14007 classes")
        result.output.contains("com.sun                        7115")
    }


    def "Check multiple signatures of cache task output"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
            
            animalsniffer {
                cache.enabled = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'                
                compile 'org.slf4j:slf4j-api:1.7.25'
            }
            
            task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
                signature = animalsnifferCacheMain.outputs.files
                depth = 2
            } 

        """
//        debug()

        when: "run task"
        BuildResult result = run('printSignature')

        then: "task successful"
        result.task(':printSignature').outcome == TaskOutcome.SUCCESS

        then: "sun signature read"
        result.output.contains("Signature animalsnifferCacheMain_!java16-sun-1.0.sig (1.3 Mb) contains 13710 classes")
        result.output.contains("com.sun                        7115")

        then: "android signature read"
        result.output.contains("Signature animalsnifferCacheMain_!android-api-level-14-4.0_r4.sig")
        result.output.contains("org.apache                     367")
    }
}