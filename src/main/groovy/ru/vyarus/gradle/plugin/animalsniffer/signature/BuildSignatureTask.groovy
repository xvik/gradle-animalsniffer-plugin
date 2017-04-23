package ru.vyarus.gradle.plugin.animalsniffer.signature

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.*
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

/**
 * Task for building animalsniffer signature. Compiled classes, jars and other signatures may be used as source.
 * Files are required in any case, even if you just want to merge existing signatures (ant task limitation).
 * <p>
 * Task may be used directly or through registered `animalsnifferSignature' configuration closure.
 * <p>
 * AnimalsnifferClasspath will be set from 'animalsniffer' configuration. By default, output file
 * is `build/animalsniffer/${task.name}.sig`.
 * <p>
 * TProperties may be used for configuration directly, but it will be more convenient to use provided helper
 * methods instead.
 *
 * @author Vyacheslav Rusakov
 * @since 12.04.2017
 * @see AnimalSnifferSignatureExtension for configuration extension
 * @see <a href="http://www.mojohaus.org/animal-sniffer/animal-sniffer-ant-tasks/examples/generating-signatures.html">
 *     ant task docks</a>
 */
@CompileStatic
@CacheableTask
@SuppressWarnings('ConfusingMethodName')
class BuildSignatureTask extends DefaultTask {

    /**
     * The class path containing the Animal Sniffer library to be used.
     * Default set by plugin.
     */
    @Classpath
    @InputFiles
    FileCollection animalsnifferClasspath

    /**
     * Signature files to extend.
     */
    @InputFiles
    @Optional
    FileCollection signatures

    /**
     * Compiled classes and jars.
     * Must be always declared.
     */
    @InputFiles
    FileCollection files

    /**
     * Include only packages ('com.java.*')
     */
    @Input
    @Optional
    Set<String> include = []

    /**
     * Exclude packages ('com.sun.*')
     */
    @Input
    @Optional
    Set<String> exclude = []

    /**
     * Output file. Default set by plugin.
     */
    @OutputFile
    File output

    @Inject
    Instantiator getInstantiator() {
        throw new UnsupportedOperationException()
    }

    @Inject
    IsolatedAntBuilder getAntBuilder() {
        throw new UnsupportedOperationException()
    }

    @TaskAction
    @CompileStatic(TypeCheckingMode.SKIP)
    void run() {
        antBuilder.withClasspath(getAnimalsnifferClasspath()).execute { a ->
            ant.taskdef(name: 'buildSignature', classname: 'org.codehaus.mojo.animal_sniffer.ant.BuildSignaturesTask')
            ant.buildSignature(destfile: getOutput().absolutePath) {
                if (files && !files.empty) {
                    path(path: files.asPath)
                }
                if (signatures && !signatures.empty) {
                    signatures.files.each {
                        signature(src: it.absolutePath)
                    }
                }
                include.each {
                    includeClasses(className: it)
                }
                exclude.each {
                    excludeClasses(className: it)
                }
            }
        }
    }

    /**
     * Files to use for signature generation. Includes classes (compiled) or jars.
     * Internally {@link org.gradle.api.Project#files(java.lang.Object ...)} is used so any definition may be used,
     * supported by gradle method.
     * <p>
     * Method may be called multiple times - all inputs will aggregate.
     * <p>
     * Common cases:
     * <ul>
     * <li>sourceSets.main.output - all compiled classes of source set</li>
     * <li>configurations.mycustom - all jars, defined in 'mycustom' configuration (of course, standard
     * configurations like 'compile' or 'test' could also be used)</li>
     * <ul>
     *
     * @param input input files (classes ot jars)
     */
    void files(Object input) {
        if (files == null) {
            files = project.files(input)
        } else {
            files = files + project.files(input)
        }
    }

    /**
     * Signatures to extend from (use all definitions from signature). May be used only together with files definition
     * (for example, you can't just merge multiple signatures; this is ant task limitation).
     * <p>
     * Method may be called multiple times - all inputs will aggregate.
     * <p>
     * Plugin register special 'signature' configuration to use for signatures definition and it makes sense to use
     * it for signatures definition for build task. In this case, use {@code configurations.signature} to add
     * all defined signatures.
     * <p>
     * Internally {@link org.gradle.api.Project#files(java.lang.Object ...)} is used so any definition may be used,
     * supported by gradle method.
     *
     * @param signature base signatures
     */
    void signatures(Object signature) {
        if (signatures == null) {
            signatures = project.files(signature)
        } else {
            signatures = signatures + project.files(signature)
        }
    }

    /**
     * Use to include only defined classes in resulted signature.
     * For example: specify 'com.java.*' to include only 'com.java' package and all it's sub packages.
     * <p>
     * Method may be called multiple times - all inputs will aggregate.
     *
     * @param path class names or package mask
     */
    void include(String... path) {
        include.addAll(path)
    }

    /**
     * Use to exclude classes in resulted signature.
     * For example: specify 'com.sun.*' to exclude 'com.sun' package and all it's sub packages.
     * <p>
     * Method may be called multiple times - all inputs will aggregate.
     *
     * @param path class names or package mask
     */
    void exclude(String... path) {
        exclude.addAll(path)
    }

    /**
     * Use to override target signature file name. By default, name will be the same as task name.
     *
     * @param name signature file name
     */
    void outputName(String name) {
        output = new File(project.buildDir, "animalsniffer/${name}.sig")
    }
}
