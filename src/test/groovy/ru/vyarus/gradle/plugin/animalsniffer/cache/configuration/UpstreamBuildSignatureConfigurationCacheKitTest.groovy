package ru.vyarus.gradle.plugin.animalsniffer.cache.configuration

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import ru.vyarus.gradle.plugin.animalsniffer.UpstreamKitTest
import ru.vyarus.gradle.plugin.animalsniffer.info.SignatureReader

/**
 * @author Vyacheslav Rusakov
 * @since 12.12.2024
 */
class UpstreamBuildSignatureConfigurationCacheKitTest extends AbstractKitTest {

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
        BuildResult result = runVer(UpstreamKitTest.GRADLE_VERSION, 'animalsnifferSignature', '--configuration-cache', '--configuration-cache-problems=warn')

        then: "no configuration cache incompatibilities"
        result.output.contains("1 problem was found storing the configuration cache")
        result.output.contains('Gradle runtime: support for using a Java agent with TestKit')
        result.output.contains('Calculating task graph as no cached configuration is available for tasks:')

        then: "task successful"
        result.task(':animalsnifferSignature').outcome == TaskOutcome.SUCCESS

        then: "validate signature"
        SignatureReader.readSignature(file("build/animalsniffer/signature/${projectName()}.sig")) == [
                'ann.Sample',
                'valid.Sample'
        ]

        when: "run from cache"
        println '\n\n------------------- FROM CACHE ----------------------------------------'
        result = runVer(UpstreamKitTest.GRADLE_VERSION, 'animalsnifferSignature', '--configuration-cache', '--configuration-cache-problems=warn')

        then: "cache used"
        result.output.contains('Reusing configuration cache.')

//        then: "task successful"
//        result.task(':animalsnifferSignature').outcome == TaskOutcome.SUCCESS
//
//        then: "validate signature"
//        SignatureReader.readSignature(file("build/animalsniffer/signature/${projectName()}.sig")) == [
//                'ann.Sample',
//                'valid.Sample'
//        ]
    }

}
