package ru.vyarus.gradle.plugin.animalsniffer.signature

import org.gradle.api.Project
import ru.vyarus.gradle.plugin.animalsniffer.AbstractTest
import ru.vyarus.gradle.plugin.animalsniffer.AnimalSniffer

/**
 * @author Vyacheslav Rusakov
 * @since 24.04.2017
 */
class BuildSignatureTaskTest extends AbstractTest {

    def "Check no default build task registration"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"
        }

        then: "tasks registered"
        project.tasks.withType(AnimalSniffer).size() == 2
        project.tasks.withType(BuildSignatureTask).empty
    }


    def "Check default build task registration"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            animalsnifferSignature {
                files sourceSets.main.output
            }
        }

        then: "tasks registered"
        project.tasks.withType(AnimalSniffer).size() == 2
        project.tasks.withType(BuildSignatureTask).size() == 1

        then: "defaults correct"
        BuildSignatureTask task = project.tasks.animalsnifferSignature
        task.output.canonicalPath.replace('\\', '/').endsWith("build/animalsniffer/${project.name}.sig")
        task.animalsnifferClasspath != null
    }

    def "Check custom task defaults"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            tasks.create('sig', ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask) {
                files sourceSets.main.output
            }
        }

        then: "defaults correct"
        BuildSignatureTask task = project.tasks.sig
        task.output.canonicalPath.replace('\\', '/').endsWith("build/animalsniffer/sig.sig")
        task.animalsnifferClasspath != null
    }
}
