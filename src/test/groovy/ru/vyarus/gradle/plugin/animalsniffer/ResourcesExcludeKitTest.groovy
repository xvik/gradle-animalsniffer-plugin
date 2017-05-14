package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 15.05.2017
 */
class ResourcesExcludeKitTest extends AbstractKitTest {

    def "Check signature of resources task output"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
            
            animalsniffer {
                resourcesExclude 'com.sun.*', 'javax.swing.*'
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
            }
            
            task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
                signature = animalsnifferResourcesMain.outputs.files
                depth = 2
            } 

        """
//        debug()

        when: "run task"
        BuildResult result = run('printSignature')

        then: "task successful"
        result.task(':printSignature').outcome == TaskOutcome.SUCCESS
        result.output.contains("Signature animalsnifferResourcesMain.sig")
        result.output.contains("contains 4847 classes")
        !result.output.contains("com.sun")
    }
}