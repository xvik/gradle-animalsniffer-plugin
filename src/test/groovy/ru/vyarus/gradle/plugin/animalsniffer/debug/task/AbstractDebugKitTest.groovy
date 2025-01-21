package ru.vyarus.gradle.plugin.animalsniffer.debug.task

import org.gradle.testkit.runner.BuildResult
import ru.vyarus.gradle.plugin.animalsniffer.android.AbstractAndroidKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 02.12.2024
 */
abstract class AbstractDebugKitTest extends AbstractAndroidKitTest {

    String extractReport(BuildResult result) {
        String out = result.output
        int idx = out.indexOf('== [')
        if (idx > 0) {
            out = out.substring(idx)
        }
        idx = out.lastIndexOf('\\===================')
        if (idx > 0) {
            idx = out.indexOf('\n', idx)
            if (idx > 0) {
                // also include last \n
                out = out.substring(0, idx + 1)
            }
        }

        return out?.trim()
    }

    String readReport(String name) {
        String file = "/${getClass().name.replace('.', '/')}_${name}.txt"
        println "Reading report: $file"
        readFileFromClasspath(file)?.replace('\r', '')?.trim()
    }
}
