package ru.vyarus.gradle.plugin.animalsniffer.signature

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import ru.vyarus.gradle.plugin.animalsniffer.signature.support.SignatureReader

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

            task sig(type: ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask) {
                files sourceSets.main.output
                files configurations.compile
            }

            repositories { mavenCentral()}
            dependencies {
                compile 'junit:junit:4.12'
                compile "org.codehaus.mojo:animal-sniffer-annotations:1.14"
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
        List<String> sigs = SignatureReader.readSignature(file('build/animalsniffer/sig.sig'))
        sigs.size() > 0
        sigs.contains('ann.Sample')
        sigs.contains('valid.Sample')
        sigs.contains('org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement')
        sigs.contains('org.junit.Rule')
    }

}
