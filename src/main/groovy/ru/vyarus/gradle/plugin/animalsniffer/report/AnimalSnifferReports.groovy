package ru.vyarus.gradle.plugin.animalsniffer.report

import org.gradle.api.reporting.ReportContainer
import org.gradle.api.reporting.SingleFileReport

/**
 * Supported AnimalSniffer reports.
 *
 * @author Vyacheslav Rusakov
 * @since 14.12.2015
 */
interface AnimalSnifferReports extends ReportContainer<SingleFileReport> {

    /**
     * @return Animal-sniffer text report
     */
    SingleFileReport getText()
}
