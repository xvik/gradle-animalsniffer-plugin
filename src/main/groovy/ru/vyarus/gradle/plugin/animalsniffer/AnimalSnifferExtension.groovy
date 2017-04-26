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
     * Shortcut for {@link #ignore}.
     *
     * @param classes one or more classes, not mentioned in signatures
     */
    @SuppressWarnings('ConfusingMethodName')
    void ignore(String... classes) {
        ignore.addAll(classes)
    }
}
