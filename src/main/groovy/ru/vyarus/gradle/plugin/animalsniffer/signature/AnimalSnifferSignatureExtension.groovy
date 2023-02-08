package ru.vyarus.gradle.plugin.animalsniffer.signature

import groovy.transform.CompileStatic

/**
 * Animalsniffer signature build extension. Registered as 'animalsnifferSignature' configuration.
 * When used, custom task 'animalsnifferSignature' is registered for building signature.
 * <p>
 * Properties may be used for configuration directly, but it will be more convenient to use provided methods instead.
 *
 * @author Vyacheslav Rusakov
 * @since 23.04.2017
 * @see BuildSignatureTask for signature build task (which may be used directly)
 */
@CompileStatic
@SuppressWarnings('ConfusingMethodName')
class AnimalSnifferSignatureExtension {

    List<Object> files = []
    List<Object> signatures = []
    Set<String> include = []
    Set<String> exclude = []
    String outputName
    /**
     * Enable plugin configuration debug output.
     */
    boolean debug = false

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
        this.files << input
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
        this.signatures << signature
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
        this.include.addAll(path)
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
        this.exclude.addAll(path)
    }

    /**
     * @return true if anything is configured and signature build task must be attached
     */
    boolean isConfigured() {
        return !signatures.empty || !files.empty
    }
}
