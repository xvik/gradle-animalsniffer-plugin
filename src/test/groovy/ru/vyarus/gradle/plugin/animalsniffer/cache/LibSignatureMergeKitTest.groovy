package ru.vyarus.gradle.plugin.animalsniffer.cache

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 18.07.2017
 *
 * @see ru.vyarus.gradle.plugin.animalsniffer.LibSignatureKitTest paired test
 */
class LibSignatureMergeKitTest extends AbstractKitTest {

    def "Check library signature merge using cache"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
            
            configurations.create('newsig')

            task sig(type: ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask) {
                files configurations.newsig
            }

            animalsniffer {
                ignoreFailures = true
                signatures = files(configurations.signature, sig.outputFiles)
                excludeJars 'slf4j-*'
                cache {
                    enabled = true
                    mergeSignatures = true
                }
            }                        

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                // dependency that must to be excluded to be able to check with newly generated signature
                implementation 'org.slf4j:slf4j-api:1.7.25'
                
                // configuration used only to build signature
                newsig 'org.slf4j:slf4j-api:1.5.3'
            }

        """
        fileFromClasspath('src/main/java/custsig/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/custsig/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('animalsnifferMain')

        then: "task successful"
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS

        then: "found slf4j violation"
        result.output.contains("1 AnimalSniffer violations were found in 1 files")
        result.output.replaceAll('\r', '').contains(
                """[Undefined reference] custsig.(Sample.java:12)
  >> boolean org.slf4j.Marker.hasReferences()
""")
    }
}
