package ru.vyarus.gradle.plugin.animalsniffer.debug.collector

import com.android.build.gradle.api.BaseVariant
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.SourceSetInfo
import ru.vyarus.gradle.plugin.animalsniffer.debug.model.AndroidVariantInfo

import java.util.function.Consumer

/**
 * Resolve android variants.
 *
 * @author Vyacheslav Rusakov
 * @since 24.11.2024
 */
@CompileStatic(TypeCheckingMode.SKIP)
class AndroidVariantsInfoCollector {

    List<AndroidVariantInfo> collect(Project project, boolean collectClasspath) {
        // ApplicationAndroidComponentsExtension or LibraryAndroidComponentsExtension (AndroidComponentsExtension)
//        AndroidComponentsExtension ext = project.extensions.getByName("androidComponents")

        // BaseExtension
        Object ext = project.extensions.getByName('android')
        // AppExtension or LibraryExtension
        DomainObjectSet<BaseVariant> variants = project.plugins.findPlugin('com.android.application') ?
                ext.applicationVariants
                : ext.libraryVariants
        List<AndroidVariantInfo> res = []
        // com.android.build.gradle.api.BaseVariant
        variants.forEach(new Consumer() {
            @Override
            void accept(Object variant) {
                res.add(process(variant, collectClasspath, null))
            }
        })
        // com.android.build.gradle.TestedExtension
        ext.testVariants.forEach(new Consumer() {
            @Override
            void accept(Object variant) {
                res.add(process(variant, collectClasspath, 'test'))
            }
        })
        ext.unitTestVariants.forEach(new Consumer() {
            @Override
            void accept(Object variant) {
                res.add(process(variant, collectClasspath, 'unit test'))
            }
        })

        return res
    }

    private AndroidVariantInfo process(Object variant, boolean collectClasspath, String type) {
        List<SourceSetInfo> ss = []
        // com.android.build.gradle.internal.api.BaseVariantImpl
        variant.sourceSets.each {
            ss.add(new SourceSetInfo(name: it.name, sourceDirs: it.kotlinDirectories))
        }
        return new AndroidVariantInfo(name: variant.name,
                compileTaskName: variant.javaCompileProvider.get().name,
                sourceSets: ss,
                classes: [variant.javaCompileProvider.get().destinationDirectory.get().asFile],
                classpath: collectClasspath ? variant.javaCompileProvider.get().classpath.files : [],
                testType: type)
    }
}
