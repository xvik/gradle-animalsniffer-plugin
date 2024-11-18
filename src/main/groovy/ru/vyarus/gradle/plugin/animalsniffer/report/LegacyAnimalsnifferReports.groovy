package ru.vyarus.gradle.plugin.animalsniffer.report

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.reporting.internal.TaskGeneratedSingleFileReport
import org.gradle.api.reporting.internal.TaskReportContainer

/**
 * AnimalSniffer reports implementation. Compatible with gradle <= 8.10
 *
 * @author Vyacheslav Rusakov
 * @since 14.12.2015
 */
@CompileStatic
class LegacyAnimalsnifferReports extends TaskReportContainer<SingleFileReport> implements AnimalSnifferReports {

    private static final String TEXT = 'text'

    LegacyAnimalsnifferReports(Task task, CollectionCallbackActionDecorator callbackActionDecorator) {
        super(SingleFileReport, task, callbackActionDecorator)

        add(TaskGeneratedSingleFileReport, TEXT, task)
    }

    @Override
    SingleFileReport getText() {
        return getByName(TEXT)
    }

    @Override
    void text(Action<SingleFileReport> action) {
        action.execute(text)
    }
}
