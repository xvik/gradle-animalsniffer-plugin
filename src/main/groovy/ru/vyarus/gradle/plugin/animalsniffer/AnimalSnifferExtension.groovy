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
     * Enables check task pre-processing to optimize subsequent check task calls. Useful when check called often
     * without clean. Could be extremely helpful on large classpath (like gradle plugin project).
     * <p>
     * NOTE: it is not compatible with case when you want to check against multiple signatures: for example,
     * jdk6 and android api. In this case do not enable resources tasks (otherwise it will merge signatures).
     * <p>
     * When enabled, extra task used for each animalsniffer (check) task to build source set specific signature:
     * it will merge all configured signatures (from signature configuration) and all jars from classpath.
     * <p>
     * Enabled resource tasks allows using multiple signature declarations for check: they will be merged and so
     * used "at once" (all of them at one check and not check per signature).
     * <p>
     * More likely, resource tasks could be enabled to greatly optimize check task speed for projects with
     * large classpath because, without extra task, animalsniffer have to always process all jars in classpath.
     * Resources task allow reading all jars only once and use pre-build specific signature for all subsequent
     * check runs (much faster). Moreover, this approach allows removing not using apis from the generated signature
     * (see {@link #resourcesExclude} below) to reduce signature size and speed up check even more.
     * <p>
     * In some cases, resource tasks may fail due to merge conflicts. For exampele, jdk 8 signature could not be merged
     * with gradle jar because of different xml apis. But in most cases, there will not be any problems.
     */
    boolean useResourcesTask = false

    /**
     * Option ignored until {@link #useResourcesTask} is enabled (disabled by default). Specifies exclusions for
     * source set specific signature (resources task, used before each check task). Exclusions make generated signature
     * smaller and, as a result, faster check executions.
     * <p>
     * PAY ATTENTION: by default, 'sun.*' and repackaged dependencies in gradle are excluded (the last one makes
     * check much much faster on gradle plugin projects).
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
