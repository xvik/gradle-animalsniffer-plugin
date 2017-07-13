package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.api.Project
import ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask

/**
 * @author Vyacheslav Rusakov
 * @since 11.05.2017
 */
class CheckCacheTaskDeactivationTest extends AbstractTest {

    def "Check simple mode"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                cache.enabled = false
            }
        }

        then: "check task registered without cache task"
        project.tasks.withType(AnimalSniffer).size() == 2
        project.tasks.withType(BuildSignatureTask).size() == 0
    }


    def "Check cache task creation"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                cache.enabled = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
            }
        }

        then: "check task registered with cache task"
        project.tasks.withType(AnimalSniffer).size() == 2
        project.tasks.withType(BuildSignatureTask).size() == 2
        def first = project.tasks.withType(BuildSignatureTask).first()
        first.getOnlyIf().isSatisfiedBy(first)
    }
}
