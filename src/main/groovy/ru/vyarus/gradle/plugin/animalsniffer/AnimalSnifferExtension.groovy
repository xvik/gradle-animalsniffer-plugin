package ru.vyarus.gradle.plugin.animalsniffer

import groovy.transform.CompileStatic
import org.gradle.api.plugins.quality.CodeQualityExtension

/**
 * Animal sniffer plugin extension. Use 'sourceSets' to define target source set (all by default).
 * Use 'toolVersion' to override default version. 'ignoreFailures' prevents failures on error.
 *
 * @author Vyacheslav Rusakov
 * @since 13.12.2015
 */
@CompileStatic
class AnimalSnifferExtension extends CodeQualityExtension {

    AnimalSnifferExtension() {
        toolVersion = '1.15'
    }

    /**
     * Annotation class name used to disable check for annotated class/method/field.
     */
    String annotation

    /**
     * Ignore classes, not mentioned in signature. This does not mean "not check class", this mean "allow class usage".
     * Useful in situations, when some classes target higher java versions and so may use classes not described in
     * signature.
     * <p>
     * Shortcut method may be used instead of direct collection assignment (shorter for single record).
     * <p>
     * Note: asterisk may be used to ignore all classes in package: com.pkg.*
     * <p>
     * See <a href="http://www.mojohaus.org/animal-sniffer/animal-sniffer-ant-tasks/examples/checking-signatures.html
     * #Ignoring_classes_not_in_the_signature">
     * docs</a> for more info.
     */
    Collection<String> ignore = []

    /**
     * When enabled, extra task used for each animalsniffer (check) task to build project-specific signature
     * from specified signatures and project classpath. This allows using multiple signature declarations for check
     * and speeds-up subsequent animalsniffer calls. It greatly optimize speed for projects with large classpath
     * because, without extra task, animalsniffer have to always process all jars in classpath.
     * <p>
     * As a downside, first run will be a bit slower because of an extra time required for signature build.
     * For projects with very small classpath resources task usage does not make sense and may be disabled.
     * <p>
     * When disabled, multiple configured signatures will be handled subsequently, as before.
     */
    boolean useResourcesTask = true

    /**
     * {@code useResourcesTask} must be enabled, otherwise option ignored. Specifies exclusions for source set
     * specific signature (resources task, used before each check task). Exclusions make generated signature
     * smaller and, as a result, faster check executions.
     * <p>
     * By default, 'sun.*' and repackaged dependencies in gradle are excluded.
     *
     * @see ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask to look for packages to exclude
     * @see ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask#exclude (alias to)
     */
    Collection<String> resourcesExclude = [
            //'com.sun.*',                       // 7115 classes in jdk6 (out of 18312)
            'sun.*',                            // 4636 classes in jdk6 (out of 18312) .. must never be used
            //'javax.swing.*',                   // 1781 classes in jdk6 (out of 18312)
            //'org.gradle.internal.*',           // 17402 classes for gradle 3.3 (in contrast to 3285 in gradle.api)
            'org.gradle.internal.impldep.*', // 16239 classes - repackaged 3rd parties
            //'org.gradle.api.internal.*',       // 2026 classes (out of 3285 in gradle.api).. very likely to be used
    ]

    /**
     * Shortcut for {@link #ignore}.
     *
     * @param classes one or more classes, not mentioned in signatures
     */
    @SuppressWarnings('ConfusingMethodName')
    void ignore(String... classes) {
        ignore.addAll(classes)
    }

    /**
     * Shortcut for {@link #resourcesExclude}.
     *
     * @param exclude packages to exclude from generated source set signature
     */
    @SuppressWarnings('ConfusingMethodName')
    void resourcesExclude(String... exclude) {
        resourcesExclude.addAll(exclude)
    }
}
