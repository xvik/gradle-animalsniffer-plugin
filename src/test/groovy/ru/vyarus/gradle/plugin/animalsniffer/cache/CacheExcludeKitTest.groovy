package ru.vyarus.gradle.plugin.animalsniffer.cache

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 15.05.2017
 */
class CacheExcludeKitTest extends AbstractKitTest {

    def "Check signature of cache task output"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
            
            animalsniffer {
                cache {
                    enabled = true
                    exclude 'com.sun.*', 'javax.swing.*'
                }
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
            }
            
            task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
                signature = cacheAnimalsnifferMainSignatures.outputFiles
                depth = 2
            } 

        """
//        debug()

        when: "run task"
        BuildResult result = run('printSignature')

        then: "task successful"
        result.task(':printSignature').outcome == TaskOutcome.SUCCESS
        result.output.contains("Signature animalsnifferMainCache.sig")
        result.output.contains("contains 4780 classes")
        !result.output.contains("com.sun")
    }
}