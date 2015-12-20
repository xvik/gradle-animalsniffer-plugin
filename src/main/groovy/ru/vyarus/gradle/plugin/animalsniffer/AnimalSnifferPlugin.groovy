package ru.vyarus.gradle.plugin.animalsniffer

import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.plugins.quality.internal.AbstractCodeQualityPlugin
import org.gradle.api.tasks.SourceSet

/**
 * AnimalSniffer plugin. Implemented the same way as gradle quality plugins (checkstyle, pmd, findbugs):
 * main configuration 'animalsniffer' used to configure custom tasks, registered for each source set.
 * <p>
 * Signatures are expected to be defined with 'signature' configuration. Multiple signatures may be set.
 * If no signatures defined - no check will be performed.
 * <p>
 * Reports configuration is performed with reports section. Only text report supported. All violations
 * are always printed to console.
 *
 * @author Vyacheslav Rusakov
 * @since 13.12.2015
 */
class AnimalSnifferPlugin extends AbstractCodeQualityPlugin<AnimalSniffer> {

    private static final String SIGNATURE_CONF = 'signature'
    private static final String ANIMALSNIFFER_CONF = 'animalsniffer'

    private AnimalSnifferExtension extension

    @Override
    protected String getToolName() {
        return 'AnimalSniffer'
    }

    @Override
    protected Class<AnimalSniffer> getTaskType() {
        return AnimalSniffer
    }

    @Override
    @SuppressWarnings('BuilderMethodWithSideEffects')
    protected void createConfigurations() {
        super.createConfigurations()
        project.configurations.create(SIGNATURE_CONF).with {
            visible = false
            transitive = true
            description = 'AnimalSniffer signatures'
        }
    }

    @Override
    protected CodeQualityExtension createExtension() {
        extension = project.extensions.create(ANIMALSNIFFER_CONF, AnimalSnifferExtension)
        extension.with {
            toolVersion = '1.14'
        }
        return extension
    }

    @Override
    protected void configureTaskDefaults(AnimalSniffer task, String baseName) {
        Configuration signatures = project.configurations[SIGNATURE_CONF]
        Configuration animalsnifferConfiguration = project.configurations[ANIMALSNIFFER_CONF]
        animalsnifferConfiguration.defaultDependencies { dependencies ->
            dependencies.add(this.project.dependencies
                    .create("org.codehaus.mojo:animal-sniffer-ant-tasks:${this.extension.toolVersion}"))
        }
        task.conventionMapping.with {
            animalsnifferSignatures = { signatures }
            animalsnifferClasspath = { animalsnifferConfiguration }
            ignoreFailures = { extension.ignoreFailures }
            annotation = { extension.annotation }
        }

        task.reports.all { report ->
            report.conventionMapping.with {
                enabled = { true }
                destination = { new File(extension.reportsDir, "${baseName}.${report.name}") }
            }
        }
    }

    @Override
    protected void configureForSourceSet(SourceSet sourceSet, AnimalSniffer task) {
        task.with {
            description = "Run AnimalSniffer checks for ${sourceSet.name} classes"
        }
        task.setSource(sourceSet.output)
        task.conventionMapping.with {
            classpath = { sourceSet.compileClasspath }
            classes = {
                // the simple "classes = sourceSet.output" may lead to non-existing resources directory
                // being passed to FindBugs Ant task, resulting in an error
                project.fileTree(sourceSet.output.classesDir) {
                    builtBy sourceSet.output
                }
            }
            sourcesDirs = { sourceSet.allJava }
        }
    }
}
