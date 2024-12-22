package ru.vyarus.gradle.plugin.animalsniffer.support.reactor

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/**
 * Kotlin multiplatform source sets reactor.
 *
 * @author Vyacheslav Rusakov
 * @since 19.12.2024
 */
@CompileStatic
class MultiplatformSourceSetsReactor implements TargetReactor<Object> {

    private final Project project

    MultiplatformSourceSetsReactor(Project project) {
        this.project = project
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    // org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
    void onTarget(Action<Object> action) {
        // org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
        NamedDomainObjectCollection<KotlinSourceSet> sourceSets = project.extensions
                .getByName('kotlin').sourceSets

        sourceSets.all { sourceSet ->
            action.execute(sourceSet)
        }
    }
}
