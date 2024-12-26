package ru.vyarus.gradle.plugin.animalsniffer

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.quality.CodeQualityExtension
import ru.vyarus.gradle.plugin.animalsniffer.util.TargetType

/**
 * Animal sniffer plugin extension. Use 'sourceSets' to define target source set (all by default).
 * Use 'toolVersion' to override default version. 'ignoreFailures' prevents failures on error.
 *
 * @author Vyacheslav Rusakov
 * @since 13.12.2015
 */
@CompileStatic
class AnimalSnifferExtension extends CodeQualityExtension {

    private final Project project

    AnimalSnifferExtension(Project project) {
        this.project = project
        toolVersion = '1.24'
    }

    /**
     * Enable plugin configuration debug output.
     */
    boolean debug = false

    /**
     * Signatures to use for the check. By default {@code configurations.signature}.
     * Assumed to be used for specific cases when signature for check is built inside project
     * or come from not standard source.
     */
    FileCollection signatures = project.configurations[AnimalSnifferPlugin.SIGNATURE_CONF]

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
     * Jar names to exclude from classpath. Asterisk should be used to mask version part 'slf4j-*'.
     * File names in classpath will be named as artifactId-version (note extension is not counted!).
     * <p>
     * Pattern name is actually a regexp. Asterisk is just a simplification, which is replaced to '.+' in real
     * pattern.
     * <p>
     * This is required for specific cases when you use 3rd party library signatures and
     * need to exclude library jars to correctly check with signature.
     */
    Collection<String> excludeJars = []

    /**
     * Check task has to always load and parse entire classpath. This could be time consuming on large classpath.
     * When cache is enabled, classpath is loaded just once and converted to a signature file, which is much faster to
     * load.
     * <p>
     * Cache is disabled by default because of problematic cases when both signature and jars contains the same classes
     * (animalsniffer limitation). Moreover, plugin exclude some rarely used classes from the signature which
     * could lead to confusion (if would be enabled by default).
     */
    CheckCacheExtension cache = new CheckCacheExtension()

    /**
     * When enabled, animalsniffer tasks for test source sets would be added as a dependency for check task.
     * Check source detected by containing "test" work in it's name. Applies for source sets, kotlin platforms and
     * android variants.
     * <p>
     * This is a simpler alternative for a common configuration: sourceSets = [project.sourceSets.main]
     * Option overrides {@link #sourceSets} configuration: even if test source set would be defined, task would not
     * be run by default (with check task) until this boolean option become true.
     */
    boolean checkTestSources = false

    /**
     * Supersedes old {@link #sourceSets} configuration which still would be counted ONLY if this list would be empty.
     * <p>
     * Defines required targets to assign animalsniffer tasks
     * into check task (what animalsniffer tasks should run by default).
     * <p>
     * Naming is not strict: target must contain provided name. For example, if set {@code ['main']} then
     * both "animalsnifferMain" and animalsnifferDesktopMain would be assigned to check task.
     * <p>
     * This method covers tasks from all sources: source sets, kotlin multiplatform and android variants.
     * In case of android there are a lot of tasks, but they usually share some nameing conventions
     * (like "main" in name).
     * <p>
     * Note that by default all test targets are avoided with {@link #checkTestSources}.
     * <p>
     * To see the full list of animalsniffer tasks use {@link #debug} option.
     */
    Set<String> defaultTargets = []

    /**
     * A way to hide animalsniffer tasks of particular type (prevent assigning animalsniffer task as check dependency).
     * For example, if you want to ignore multiplatform tasks entirely:
     * {@code ignoreTargets = [ TargetType.MultiplatformTarget ]} and all animalsniffer tasks related to kotlin
     * multiplatform would not be added to check task.
     */
    Set<TargetType> ignoreTargets = []

    /**
     * @param cache cache configuration closure
     */
    void setCache(@DelegatesTo(value = CheckCacheExtension) Closure cache) {
        project.configure(this.cache, cache)
    }

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
     * Shortcut for {@link #excludeJars}.
     *
     * @param names jar name patterns to exclude
     */
    @SuppressWarnings('ConfusingMethodName')
    void excludeJars(String... names) {
        excludeJars.addAll(names)
    }
}
