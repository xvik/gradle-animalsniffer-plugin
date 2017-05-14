package ru.vyarus.gradle.plugin.animalsniffer.info

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

/**
 * Special task to analyze signature. Based on the analysis you can exclude some not used packages
 * to get smaller signature. Supposed to be used for performance tuning of signature generation tasks (from classpath)
 * used by check tasks (the smaller signature they produce - the faster check task could run).
 * <p>
 * Task prints to console amount of classes in packages for configured depth.
 *
 * @author Vyacheslav Rusakov
 * @since 14.05.2017
 */
@CacheableTask
@CompileStatic
class SignatureInfoTask extends DefaultTask {

    private static final int EDGE = 1024
    private static final String DOT = '.'
    private static final int PKG_SP = 10

    /**
     * Signature files to analyze. Accepts file collection to simplify lazy configuration.
     * Most likely, it would be {@code configurations.signature} and {@code animalsnifferResourcesMain.outputs.files}.
     */
    @SkipWhenEmpty
    @InputFiles
    FileCollection signature

    /**
     * Aggregation packages depth.
     */
    @Input
    @Optional
    int depth = 2

    /**
     * When true, sort output by package size - packages with more classes goes top.
     * Useful for reducing signature. When false, use signature order - most likely alphabetical.
     */
    @Input
    @Optional
    boolean sortBySize = true

    @TaskAction
    void action() {
        getSignature().files.each { File signature ->
            int dpth = getDepth()
            List<String> sig = SignatureReader.readSignature(signature)
            logger.warn("Signature ${signature.name} (${formatSize(signature.size())}) contains ${sig.size()} classes")

            Map<String, Integer> summary = [:]
            sig.each {
                String[] path = toPaths(it)
                // reduce to required depth
                if (path.size() > dpth) {
                    path = path.take(dpth)
                }
                String key = path.join(DOT)
                summary[key] = summary.containsKey(key) ? summary[key] + 1 : 1
            }

            if (getSortBySize()) {
                summary = summary.sort
                        { Map.Entry<String, Integer> a, Map.Entry<String, Integer> b -> -1 * (a.value <=> b.value) }
            }

            summary.each { key, val -> logger.warn(String.format("\t%-${PKG_SP + dpth * PKG_SP}s %s", key, val)) }
        }
    }

    private String formatSize(long size) {
        double preciseSize = size
        String label = ['bytes', 'Kb', 'Mb', 'Gb', 'Tb'].find {
            if (preciseSize < EDGE) {
                true
            } else {
                preciseSize /= EDGE
                false
            }
        }
        return "${String.format(Locale.ENGLISH, "%.1f", preciseSize)} $label"
    }

    private String[] toPaths(String clazz) {
        return clazz[0..clazz.lastIndexOf(DOT)].split('\\.')
    }
}
