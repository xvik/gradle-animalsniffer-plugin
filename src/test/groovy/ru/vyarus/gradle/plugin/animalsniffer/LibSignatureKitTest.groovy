package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Vyacheslav Rusakov
 * @since 17.07.2017
 *
 * @see ru.vyarus.gradle.plugin.animalsniffer.cache.LibSignatureMergeKitTest paired test
 */
class LibSignatureKitTest extends AbstractKitTest {

    def "Check classpath jars print"() {
        setup:
        build """
            plugins {
                id 'java'            
            }
                     
            
            task printClasspath {
                doLast {
                    println 'Compile configuration jars:'
                    configurations.compile.files.each { println it.name}
                }
            }

            repositories { mavenCentral()}
            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.25'
            }

        """

        when: "call print"
        BuildResult result = run('printClasspath')

        then: "success"
        result.task(":printClasspath").outcome == TaskOutcome.SUCCESS
    }

    def "Check library signature usage"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }
            
            configurations.create('newsig')

            animalsniffer {
                ignoreFailures = true
                excludeJars 'slf4j-*'
            }
            
            task sig(type: ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask) {
                signatures configurations.signature
                files configurations.newsig
            }

            repositories { mavenCentral()}
            dependencies {
                // this signature is used only to build custom signature, but not in check directly
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                // dependency that must to be excluded to be able to check with newly generated signature
                compile 'org.slf4j:slf4j-api:1.7.25'
                
                // configuration used only to build signature
                newsig 'org.slf4j:slf4j-api:1.5.3'
            }
            
            // use generated signature
            animalsnifferMain.animalsnifferSignatures = project.files(sig.outputs.files)

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