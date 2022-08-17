package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import ru.vyarus.gradle.plugin.animalsniffer.signature.AnimalSnifferSignatureExtension

/**
 * @author Vyacheslav Rusakov
 * @since 13.12.2015
 */
class AnimalSnifferPluginTest extends AbstractTest {

    def "Check extension registration"() {

        when: "plugin applied"
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply "ru.vyarus.animalsniffer"

        then: "extension not registered"
        !project.extensions.findByType(AnimalSnifferExtension)

    }

    def "Check extension validation"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"
        }

        then: "tasks registered"
        project.extensions.findByType(AnimalSnifferExtension)
        project.extensions.findByType(AnimalSnifferSignatureExtension)
        project.tasks.withType(AnimalSniffer).size() == 2
    }

    def "Check scope reduce"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                sourceSets = [sourceSets.main]
            }
        }

        then: "task registered"
        project.tasks.withType(AnimalSniffer).size() == 2
    }

    def "Tool version override"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                toolVersion = '1.10'
            }
        }
        def animalsniffer = project.configurations.animalsniffer
        // force defaults processing
        animalsniffer.runDependencyActions()

        then: "task registered"
        animalsniffer.dependencies.first().version == '1.10'
    }

    def "Check source set modifications visible"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            sourceSets {
                main {
                    java {
                        srcDir("src/main2")
                    }
                }
                itest {
                    java {
                        srcDirs("src/itest")
                    }
                }
            }
        }

        then: "task registered"
        project.tasks.withType(AnimalSniffer).size() == 3
        (project.tasks.getByName('animalsnifferMain') as AnimalSniffer).sourcesDirs.size() == 2
    }
}