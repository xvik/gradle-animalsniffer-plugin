package ru.vyarus.gradle.plugin.animalsniffer.report

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.reporting.ReportContainer
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.tasks.Internal

/**
 * Supported AnimalSniffer reports.
 *
 * @author Vyacheslav Rusakov
 * @since 14.12.2015
 */
@CompileStatic
interface AnimalSnifferReports extends ReportContainer<SingleFileReport> {

    /**
     * IMPORTANT: this report is required in order to build text report and console output (because animalsniffer
     * itself could run in another jvm).
     * <p>
     * Report columns:
     * <ul>
     *     <li>Signature name
     *     <li>Source file
     *     <li>Line number
     *     <li>Field (when problem in field)
     *     <li>Error message (when message parse failed would contain complete (unparsed) error message)
     *     <li>True when failed to parse animalsniffer error
     * </ul>
     *
     * @return internal csv report used for console output
     */
    @Internal
    SingleFileReport getCsv()

    /**
     * @return Animal-sniffer text report
     */
    @Internal
    SingleFileReport getText()

    /**
     * Configures the csv report.
     *
     * @param action The Configuration closure/action.
     */
    void csv(Action<SingleFileReport> action);

    /**
     * Configures the text report.
     *
     * @param action The Configuration closure/action.
     */
    void text(Action<SingleFileReport> action);
}
