package ru.vyarus.gradle.plugin.animalsniffer.report

import org.gradle.api.Task
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.reporting.internal.TaskGeneratedSingleFileReport
import org.gradle.api.reporting.internal.TaskReportContainer

/**
 * AnimalSniffer reports implementation.
 *
 * @author Vyacheslav Rusakov
 * @since 14.12.2015
 */
class AnimalSnifferReportsImpl extends TaskReportContainer<SingleFileReport> implements AnimalSnifferReports {

    private static final String TEXT = 'text'

    AnimalSnifferReportsImpl(Task task) {
        super(SingleFileReport, task)

        add(TaskGeneratedSingleFileReport, TEXT, task)
    }

    @Override
    SingleFileReport getText() {
        return getByName(TEXT)
    }
}
