package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.api.GradleException
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
                // disable direct check to test legacy source set appliance
                checkTestSources = true
                sourceSets = [sourceSets.main]
            }
        }

        then: "task registered"
        project.tasks.withType(AnimalSniffer).size() == 2
        def check = project.tasks.findByName('check')
        def dependencies = check.taskDependencies.getDependencies(check)
        !dependencies.contains(project.tasks.findByName('animalsnifferTest'))
        dependencies.contains(project.tasks.findByName('animalsnifferMain'))
    }

    def "Check scope reduce 2"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                // disable direct check to test legacy source set appliance
                checkTestSources = true
                defaultTargets = ['main']
            }
        }

        then: "task registered"
        project.tasks.withType(AnimalSniffer).size() == 2
        def check = project.tasks.findByName('check')
        def dependencies = check.taskDependencies.getDependencies(check)
        !dependencies.contains(project.tasks.findByName('animalsnifferTest'))
        dependencies.contains(project.tasks.findByName('animalsnifferMain'))
    }

    def "Check scope reduce 3"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                // disable direct check to test legacy source set appliance
                checkTestSources = true
                ignoreTargets = [TargetType.SourceSet]
            }
        }

        then: "task registered"
        project.tasks.withType(AnimalSniffer).size() == 2
        def check = project.tasks.findByName('check')
        def dependencies = check.taskDependencies.getDependencies(check)
        !dependencies.contains(project.tasks.findByName('animalsnifferTest'))
        !dependencies.contains(project.tasks.findByName('animalsnifferMain'))
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
                        srcDir("src/main2/java")
                    }
                }
                itest {
                    java {
                        srcDirs("src/itest/java")
                    }
                }
            }
        }

        then: "task registered"
        project.tasks.withType(AnimalSniffer).size() == 3
        (project.tasks.getByName('animalsnifferMain') as AnimalSniffer).sourcesDirs.size() == 2
        (project.tasks.getByName('animalsnifferItest') as AnimalSniffer).sourcesDirs.size() == 1
    }

    def "Check report file location override"() {

        when: "plugin configured"
        Project project = project {
            apply plugin: "java"
            apply plugin: "ru.vyarus.animalsniffer"

            tasks.withType(AnimalSniffer) {
                reports.text {
                    required = false
                    outputLocation = file('build/custom.txt')
                }
            }
        }

        then: "task registered"
        project.tasks.withType(AnimalSniffer).size() == 2
    }
}