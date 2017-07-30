package ru.vyarus.gradle.plugin.animalsniffer.signature

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import ru.vyarus.gradle.plugin.animalsniffer.info.SignatureReader

/**
 * @author Vyacheslav Rusakov
 * @since 13.07.2017
 */
class MultipleSignaturesNoMergeKitTest extends AbstractKitTest {

    def "Check multiple source signatures"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }           
                
            task sig(type: ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask) {
                signatures configurations.signature       
                mergeSignatures = false         
            }    
                        
            repositories { mavenCentral()}
            dependencies {                
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'                
                signature 'net.sf.androidscents.signature:android-api-level-24:7.0_r2@signature'
            }

        """
//        debug()

        when: "run task"
        BuildResult result = run('sig')

        then: "task successful"
        result.task(':sig').outcome == TaskOutcome.SUCCESS
        file('build/animalsniffer/sig/').listFiles().size() == 2

        then: "validate signature 1"
        List<String> sigs = SignatureReader.readSignature(file('build/animalsniffer/sig/sig_!java16-sun-1.0.sig'))
        sigs.size() > 0
        sigs.contains('java.lang.Boolean')
        sigs.contains('com.sun.media.sound.SunFileReader')

        then: "validate signature 2"
        List<String> sigs2 = SignatureReader.readSignature(file('build/animalsniffer/sig/sig_!android-api-level-24-7.0_r2.sig'))
        sigs2.size() > 0
        sigs2.contains('java.lang.Boolean')
        sigs2.contains('android.icu.lang.UProperty')
    }
}
