package ru.vyarus.gradle.plugin.animalsniffer.support.reactor

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

/**
 * Kotlin multiplatform targets reactor.
 *
 * @author Vyacheslav Rusakov
 * @since 18.12.2024
 */
@CompileStatic
class MultiplatformTargetsReactor implements TargetReactor<Object> {

    private final Project project

    MultiplatformTargetsReactor(Project project) {
        this.project = project
    }

    @Override
    // KotlinTarget
    @CompileStatic(TypeCheckingMode.SKIP)
    void onTarget(Action<Object> action) {
        // org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
        NamedDomainObjectCollection<KotlinTarget> targets = project.extensions
                .getByName('kotlin').targets

        targets.all { target ->
            action.execute(target)
        }
    }
}
