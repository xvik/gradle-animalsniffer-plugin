package ru.vyarus.gradle.plugin.animalsniffer.support.reactor

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Action
import org.gradle.api.Project

/**
 * Android variants (and test components) reactor.
 *
 * @author Vyacheslav Rusakov
 * @since 18.12.2024
 */
@CompileStatic
class AndroidComponentsReactor implements TargetReactor<Object> {

    private static final List<String> TARGET_COMPONENTS = Arrays.asList(
            'com.android.build.api.variant.Variant',
            'com.android.build.api.variant.TestComponent'
    )

    private final Project project

    AndroidComponentsReactor(Project project) {
        this.project = project
    }

    @Override
    // Component
    @CompileStatic(TypeCheckingMode.SKIP)
    void onTarget(Action<Object> action) {
        // new android api examples: https://github.com/android/gradle-recipes/tree/agp-8.7
        // ApplicationAndroidComponentsExtension
        Object androidComponents = project.extensions.getByName('androidComponents')

        // com.android.build.api.variant.Variant
        androidComponents.onVariants(androidComponents.selector().all()) { variant ->
            // classloader workaround
            List<Class> targets = TARGET_COMPONENTS.collect {
                androidComponents.class.classLoader.loadClass(it)
            }

            // com.android.build.api.variant.Component
            variant.components.each { component ->
                // // variant + tests (fixtures ignored)
                targets.each {
                    if (it.isAssignableFrom(component.class)) {
                        action.execute(component)
                    }
                }
            }
        }
    }
}
