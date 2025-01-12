package ru.vyarus.gradle.plugin.animalsniffer.worker

import groovy.transform.CompileStatic
import ru.vyarus.gradle.plugin.animalsniffer.report.ReportMessage
import ru.vyarus.gradle.plugin.animalsniffer.report.ReportParser
import ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask
import ru.vyarus.gradle.plugin.animalsniffer.util.FormatUtils

/**
 * Animalsniffer errors collector.
 *
 * @author Vyacheslav Rusakov
 * @since 10.01.2025
 */
@CompileStatic
class CheckReportCollector {

    private final Set<File> roots

    private String signature
    final List<ReportMessage> messages = []

    CheckReportCollector(Set<File> roots) {
        this.roots = roots
    }

    void setSignature(String name) {
        // cut off extension
        String res = name[0..name.lastIndexOf('.') - 1]
        // check if its a cache signature and resolve original signature name
        int delimiterPos = res.indexOf(BuildSignatureTask.SIGNATURE_DELIMITER)
        if (delimiterPos > 0) {
            res = res[delimiterPos + BuildSignatureTask.SIGNATURE_DELIMITER.length()..-1]
        }
        this.signature = res
    }

    void add(String message) {
        ReportMessage msg = FormatUtils.parse(message, roots)
        msg.signature = signature
        checkAndRemoveDuplicate(msg)
    }

    void save(File target) {
        target.parentFile.mkdirs()
        target.createNewFile()
        if (!messages.empty) {
            target.text = messages
                    .collect { ReportParser.toCsv(it) }
                    .join(System.lineSeparator())
        }
    }

    /**
     * Animalsniffer 1.16 start to check method return type (no matter if it used). This lead to error duplicates:
     * for example, for code {@code Paths.get (" / tmp ") ;}, check with jdk6 signature will detect 2 errors:
     * {@code java.nio.file.Path}, {@code java.nio.file.Path java.nio.file.Paths.get(String, String[])}
     * (return type and method call). But this is duplication (point to the same line) so first error may be skipped
     * (it is derivative from bad method call).
     * <p>
     * Also, it can duplicate some errors with and without line number - this should also be avoided
     *
     * @param msg new message
     */
    private void checkAndRemoveDuplicate(ReportMessage msg) {
        ReportMessage prev = messages.empty ? null : messages.last()
        if (prev != null
                && prev.signature == msg.signature
                && prev.source == msg.source
                && prev.line == msg.line
                && msg.code.startsWith(prev.code)) {
            messages.remove(prev)
        }
        if (!msg.line) {
            // search for the same error but with line
            if (messages.find { it.source == msg.source && it.code == msg.code }) {
                // avoid duplicate message addition
                return
            }
        }
        messages.add(msg)
    }
}
