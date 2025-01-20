package ru.vyarus.gradle.plugin.animalsniffer.report

import groovy.transform.CompileStatic
import org.slf4j.Logger
import ru.vyarus.gradle.plugin.animalsniffer.util.FormatUtils

/**
 * Builds required reports (text and console) from errors list, created by worker.
 *
 * @author Vyacheslav Rusakov
 * @since 16.12.2015
 */
@CompileStatic
class ReportBuilder {

    // when multiple signatures used, signature name should be added to error message for clarity
    private final boolean printSignatureNames

    List<ReportMessage> report = []
    Set<String> affectedFiles = []

    ReportBuilder(File report, boolean printSignatureNames) {
        this.printSignatureNames = printSignatureNames
        readErrors(report)
    }

    /**
     * @return detected errors count
     */
    int errorsCnt() {
        report.size()
    }

    /**
     * @return files with errors count
     */
    int filesCnt() {
        affectedFiles.size()
    }

    String getErrorMessage(File textReport) {
        String message = "${errorsCnt()} AnimalSniffer violations were found " +
                "in ${filesCnt()} files."

        if (textReport) {
            String reportUrl = "file:///${textReport.canonicalPath.replaceAll('\\\\', '/')}"
            message += " See the report at: $reportUrl"
        }

        return message
    }

    /**
     * Writes detected errors to report file (overriding existing content).
     *
     * @param file report file
     */
    void writeToFile(File file) {
        file.newWriter().withWriter { w ->
            w << report.collect { FormatUtils.formatForFile(it, printSignatureNames) }.join(String.format('%n'))
        }
    }

    /**
     * Writes all violations to console.
     *
     * @param logger build logger
     */
    void writeToConsole(Logger logger) {
        report.each { logger.error FormatUtils.formatForConsole(it, printSignatureNames) }
    }

    // animalsniffer executed by external worker (could be even different jvm)
    private void readErrors(File target) {
        if (target.exists()) {
            target.readLines().each {
                ReportMessage msg = ReportParser.fromCsv(it)
                if (msg) {
                    report.add(msg)
                    affectedFiles.add(msg.source)
                }
            }
        }
    }
}
