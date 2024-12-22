package ru.vyarus.gradle.plugin.animalsniffer.support.reactor

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet

/**
 * Java source sets reactor.
 *
 * @author Vyacheslav Rusakov
 * @since 18.12.2024
 */
@CompileStatic
class JavaSourceSetsReactor implements TargetReactor<SourceSet> {

    private final Project project

    JavaSourceSetsReactor(Project project) {
        this.project = project
    }

    @Override
    void onTarget(Action<SourceSet> action) {
        project.extensions.findByType(JavaPluginExtension).sourceSets.all { SourceSet sourceSet ->
            action.execute(sourceSet)
        }
    }
}
