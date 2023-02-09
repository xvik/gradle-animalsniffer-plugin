package ru.vyarus.gradle.plugin.animalsniffer.signature

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import ru.vyarus.gradle.plugin.animalsniffer.info.SignatureReader

/**
 * @author Vyacheslav Rusakov
 * @since 24.04.2017
 */
class SignatureTaskDefinitionKitTest extends AbstractKitTest {

    def "Check build signature task manual definition"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            task sig(type: BuildSignatureTask) {
                files sourceSets.main.output
                files configurations.compileClasspath
            }

            repositories { mavenCentral()}
            dependencies {
                implementation 'org.slf4j:slf4j-api:1.7.25'
                implementation "org.codehaus.mojo:animal-sniffer-annotations:1.14"
            }
        """
        fileFromClasspath('src/main/java/ann/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/ann/Sample.java')
        fileFromClasspath('src/main/java/valid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('sig')

        then: "task successful"
        result.task(':sig').outcome == TaskOutcome.SUCCESS

        then: "validate signature"
        List<String> sigs = SignatureReader.readSignature(file('build/animalsniffer/sig/sig.sig'))
        sigs.size() > 0
        sigs.contains('ann.Sample')
        sigs.contains('valid.Sample')
        sigs.contains('org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement')
        sigs.contains('org.slf4j.Logger')
    }

}
