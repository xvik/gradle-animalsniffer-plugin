package ru.vyarus.gradle.plugin.animalsniffer.report

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.model.ObjectFactory
import org.gradle.api.reporting.SingleFileReport
import ru.vyarus.gradle.plugin.animalsniffer.report.internal.Reports
import ru.vyarus.gradle.plugin.animalsniffer.report.internal.DefaultSingleFileReport

/**
 * AnimalSniffer reports implementation.
 *
 * @author Vyacheslav Rusakov
 * @since 14.12.2015
 */
@CompileStatic
class AnimalSnifferReportsImpl extends Reports<SingleFileReport> implements AnimalSnifferReports {

    private static final String TYPE_TEXT = 'text'

    AnimalSnifferReportsImpl(Task task, ObjectFactory objects) {
        super(task.project, SingleFileReport)

        addReport(objects.newInstance(DefaultSingleFileReport, TYPE_TEXT, task))
    }

    @Override
    SingleFileReport getText() {
        return getByName(TYPE_TEXT)
    }

    @Override
    void text(Action<SingleFileReport> action) {
        action.execute(text)
    }
}
