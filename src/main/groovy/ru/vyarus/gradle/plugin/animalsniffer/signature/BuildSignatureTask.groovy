package ru.vyarus.gradle.plugin.animalsniffer.signature

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.*
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.GFileUtils

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
class BuildSignatureTask extends ConventionTask {

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

    /**
     * Animalsniffer ant task does not allow empty files, but, in some cases, it could happen.
     * For example, when two or more signatures must be merged. In this case plugin could
     * specify fake file together with it's exclusion from resulted signature to overcome ant task limitation.
     * <p>
     * But even with this flag enabled, anything must be configured for {@code files} (some empty collection).
     * This may seem cumbersome, but its intentional to pay attention to this as not a normal behaviour (assumed
     * by animalsniffer ant task).
     * <p>
     * Option mainly used for animalsniffer check task, when empty classpath is quite possible (project
     * without dependencies) and so associated resources task files will be empty.
     */
    boolean allowEmptyFiles

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
        if (handleSimpleCase()) {
            return
        }
        applyFakeFilesIfRequired()
        if (!getInclude().empty || !getExclude().empty) {
            logger.info("Building signature with includes=${getInclude()} and excludes=${getExclude()}")
        }
        antBuilder.withClasspath(getAnimalsnifferClasspath()).execute { a ->
            ant.taskdef(name: 'buildSignature', classname: 'org.codehaus.mojo.animal_sniffer.ant.BuildSignaturesTask')
            ant.buildSignature(destfile: getOutput().absolutePath) {
                if (getFiles() && !getFiles().empty) {
                    path(path: getFiles().asPath)
                }
                if (getSignatures() && !getSignatures().empty) {
                    getSignatures().files.each {
                        signature(src: it.absolutePath)
                    }
                }
                getInclude().each {
                    includeClasses(className: it)
                }
                getExclude().each {
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
        if (getFiles() == null) {
            setFiles(project.files(input))
        } else {
            setFiles(getFiles() + project.files(input))
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
        if (getSignatures() == null) {
            setSignatures(project.files(signature))
        } else {
            setSignatures(getSignatures() + project.files(signature))
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
        Set<String> include = getInclude()
        include.addAll(path)
        setInclude(include)
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
        Set<String> exclude = getExclude()
        exclude.addAll(path)
        setExclude(exclude)
    }

    /**
     * Use to override target signature file name. By default, name will be the same as task name.
     *
     * @param name signature file name
     */
    void outputName(String name) {
        setOutput(new File(project.buildDir, "animalsniffer/${name}.sig"))
    }

    /**
     * Note that in case of check task, this will never be true because exclusions are always applied for resources
     * task (see {@link ru.vyarus.gradle.plugin.animalsniffer.AnimalSnifferExtension#resourcesExclude}).
     *
     * @return true if single signature used and it must be used as is (no processing required)
     */
    private boolean handleSimpleCase() {
        if (getFiles().empty && getSignatures().size() == 1 && getExclude().empty && getInclude().empty) {
            GFileUtils.copyFile(getSignatures().first(), getOutput())
            return true
        }
        return false
    }

    private void applyFakeFilesIfRequired() {
        boolean signaturesConfigured = getSignatures() != null && !getSignatures().empty
        // situation: files specified, but collection is empty (for example, empty configuration)
        if (getFiles().empty && signaturesConfigured && isAllowEmptyFiles()) {
            logger.info('Workaround empty files for signature build by adding plugin jar as file ' +
                    'with package exclusion')
            // if this will fail due to security restrictions, simply disable allowEmptyFiles config
            files(BuildSignatureTask.protectionDomain.codeSource.location)
            exclude('ru.vyarus.gradle.plugin.animalsniffer.*')
        } else if (getFiles().empty) {
            throw new GradleException("No files found in: ${getFiles()}")
        }
    }
}
