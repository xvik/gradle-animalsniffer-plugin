package ru.vyarus.gradle.plugin.animalsniffer.info

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 15.05.2017
 */
class SortInfoByNameKitTest extends AbstractKitTest {
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
                depth = 1
                sortBySize = false
            } 

        """
//        debug()

        when: "run task"
        BuildResult result = run('printSignature')

        then: "task successful"
        result.task(':printSignature').outcome == TaskOutcome.SUCCESS
        result.output.replace('\r', '').contains("""Signature java16-sun-1.0.signature (1.71 Mb) contains 18312 classes
\tcom                  7115
\tjava                 2441
\tjavax                3327
\torg                  790
\tsun                  4636
\tsunw                 3
""")
    }
}