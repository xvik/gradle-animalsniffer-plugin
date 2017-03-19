package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.api.Project

/**
 * @author Vyacheslav Rusakov
 * @since 19.03.2017
 */
class IgnoreConfigurationTest extends AbstractTest {

    def "Check single ignore configuration"() {

        when: "single ignore"
        Project project = project {
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                ignore 'sample.package'
            }
        }

        then: "configured"
        project.extensions.findByType(AnimalSnifferExtension).ignore == ['sample.package']
    }

    def "Check couple of ignore configs"() {

        when: "couple of single ignore"
        Project project = project {
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                ignore 'sample.package'
                ignore 'sample.package2'
            }
        }

        then: "configured"
        project.extensions.findByType(AnimalSnifferExtension).ignore == ['sample.package', 'sample.package2']

    }

    def "Check multiple ignores"() {

        when: "multiple ignores"
        Project project = project {
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                ignore 'sample.package',
                        'sample.package2'
            }
        }

        then: "configured"
        project.extensions.findByType(AnimalSnifferExtension).ignore == ['sample.package', 'sample.package2']

    }

    def "Check direct property"() {

        when: "direct property usage"
        Project project = project {
            apply plugin: "ru.vyarus.animalsniffer"

            animalsniffer {
                ignore  = ['sample.package',
                           'sample.package2']
            }
        }

        then: "configured"
        project.extensions.findByType(AnimalSnifferExtension).ignore == ['sample.package', 'sample.package2']
    }
}
