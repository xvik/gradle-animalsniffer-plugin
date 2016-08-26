package ru.vyarus.gradle.plugin.animalsniffer.report

import groovy.transform.CompileStatic
import org.gradle.api.reporting.ReportContainer
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.tasks.Nested

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
    @Nested
    SingleFileReport getText()
}
