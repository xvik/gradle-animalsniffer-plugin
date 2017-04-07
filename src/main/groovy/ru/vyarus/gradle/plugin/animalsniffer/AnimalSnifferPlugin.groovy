package ru.vyarus.gradle.plugin.animalsniffer

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.plugins.quality.internal.AbstractCodeQualityPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.util.GradleVersion

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
@CompileStatic
class AnimalSnifferPlugin extends AbstractCodeQualityPlugin<AnimalSniffer> {

    private static final String MINIMAL_GRADLE = '2.14'
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
    protected void beforeApply() {
        // due to base class refactor from groovy to java in gradle 2.14
        // plugin can't be launched on prior gradle versions
        GradleVersion version = GradleVersion.current()
        if (version < GradleVersion.version(MINIMAL_GRADLE)) {
            throw new GradleException("Animalsniffer plugin requires gradle $MINIMAL_GRADLE or above, " +
                    "but your gradle version is: $version.version. Use plugin version 1.0.1.")
        }
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
        extension = project.extensions.<AnimalSnifferExtension>create(ANIMALSNIFFER_CONF, AnimalSnifferExtension)
        extension.toolVersion = '1.15'
        return extension
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
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
        task.doFirst {
            if (extension.ignore) {
                task.ignoreClasses = extension.ignore
            }
            // doesn't work by convention
            task.incremental = extension.incremental
        }
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    protected void configureForSourceSet(SourceSet sourceSet, AnimalSniffer task) {
        task.description = "Run AnimalSniffer checks for ${sourceSet.name} classes"
        task.setSource(sourceSet.output)
        task.dependsOn sourceSet.classesTaskName
        task.conventionMapping.with {
            classpath = { sourceSet.compileClasspath }
            sourcesDirs = { sourceSet.allJava }
        }
    }
}
