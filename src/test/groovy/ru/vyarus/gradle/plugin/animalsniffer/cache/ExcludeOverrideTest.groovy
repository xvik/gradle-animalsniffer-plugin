package ru.vyarus.gradle.plugin.animalsniffer.cache

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import ru.vyarus.gradle.plugin.animalsniffer.info.SignatureReader

/**
 * When project has no 3rd party deps and use only one signature then fake files emulation will be enabled
 * (because build signature task cant work on empty files). Checking that fake classes will not be included in the
 * resulted signature.
 *
 * @author Vyacheslav Rusakov
 * @since 18.05.2017
 */
class ExcludeOverrideTest extends AbstractKitTest {

    def "Check signature no deps case"() {

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
            }
        """

        fileFromClasspath('src/main/java/valid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
        //debug()

        when: "run task"
        BuildResult result = run('animalsnifferCacheMain')

        then: "task successful"
        result.task(':animalsnifferCacheMain').outcome == TaskOutcome.SUCCESS

        then: "signature does not contain plugin classes"
        !SignatureReader.readSignature(file("build/animalsniffer/animalsnifferCacheMain.sig")).contains('ru.vyarus')
    }

}