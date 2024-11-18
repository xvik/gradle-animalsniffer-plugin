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
     * @return Animal-sniffer text report
     */
    @Internal
    SingleFileReport getText()

    /**
     * Configures the text report.
     *
     * @param action The Configuration closure/action.
     */
    void text(Action<SingleFileReport> action);
}
