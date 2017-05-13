package ru.vyarus.gradle.plugin.animalsniffer.info

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

import java.text.DecimalFormat

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

            summary.sort { Map.Entry<String, Integer> a, Map.Entry<String, Integer> b -> -1 * (a.value <=> b.value) }
                    .each { key, val -> logger.warn(String.format('\t%-30s %s', key, val)) }
        }
    }

    private String formatSize(long size) {
        String label = [' bytes', 'KB', 'MB', 'GB', 'TB'].find {
            if (size < EDGE) {
                true
            } else {
                size /= EDGE
                false
            }
        }
        return "${new DecimalFormat('0.##').format(size)}$label"
    }

    private String[] toPaths(String clazz) {
        return clazz[0..clazz.lastIndexOf(DOT)].split('\\.')
    }
}
