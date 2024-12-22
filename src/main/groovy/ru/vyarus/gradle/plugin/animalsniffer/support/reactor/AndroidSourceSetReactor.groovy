package ru.vyarus.gradle.plugin.animalsniffer.support.reactor

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Action
import org.gradle.api.Project

/**
 * Android source sets reactor.
 *
 * @author Vyacheslav Rusakov
 * @since 19.12.2024
 */
@CompileStatic
class AndroidSourceSetReactor implements TargetReactor<Object> {

    private final Project project

    AndroidSourceSetReactor(Project project) {
        this.project = project
    }

    @Override
    // AndroidSourceSet
    @CompileStatic(TypeCheckingMode.SKIP)
    void onTarget(Action<Object> action) {
        // com.android.build.api.dsl.CommonExtension (ApplicationExtension or LibraryExtension)
        Object android = project.extensions.getByName('android')

        // com.android.build.api.dsl.AndroidSourceSet
        android.sourceSets.all { sourceSet ->
            action.execute(sourceSet)
        }
    }
}
