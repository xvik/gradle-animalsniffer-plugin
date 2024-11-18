package ru.vyarus.gradle.plugin.animalsniffer.report.internal

import groovy.transform.CompileStatic
import org.gradle.api.Task
import org.gradle.api.file.ProjectLayout
import org.gradle.api.internal.IConventionAware
import org.gradle.api.internal.provider.DefaultProvider
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.Report
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile

import javax.inject.Inject

/**
 * {@link SingleFileReport} implementation (gradle implementation is in internal package and could change any time).
 * Supports both old and new apis (pre gradle 7 and above it).
 *
 * @author Vyacheslav Rusakov
 * @since 17.11.2024
 */
@SuppressWarnings('AbstractClassWithoutAbstractMethod')
@CompileStatic
abstract class DefaultSingleFileReport implements SingleFileReport {

    private final String name
    private final Task task

    @SuppressWarnings('AbstractClassWithPublicConstructor')
    @Inject
    DefaultSingleFileReport(String name, Task task) {
        this.name = name
        this.task = task
        required.convention(false)
        outputLocation.convention(projectLayout.file(new DefaultProvider<File>({
            return ((IConventionAware) this).conventionMapping.getConventionValue(null, 'destination', false)
        })))
    }

    @Override
    Report configure(Closure cl) {
        final Closure closure = (Closure) cl.clone()
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure.call(this)
        return this
    }

    @OutputFile
    File getDestination() {
        return outputLocation.asFile.orNull
    }

    @Internal
    boolean isEnabled() {
        return required.get()
    }

    @Override
    String getDisplayName() {
        return "Report generated by task '$task.path' ($name)"
    }

    @Override
    String getName() {
        return name
    }

    void setDestination(Provider<File> provider) {
        outputLocation.fileProvider(provider)
    }

    @Override
    void setDestination(File file) {
        outputLocation.fileValue(file)
    }

    void setEnabled(Provider<Boolean> enabled) {
        required.set(enabled)
    }

    void setEnabled(boolean enabled) {
        required.set(enabled)
    }

    @Override
    OutputType getOutputType() {
        return OutputType.FILE
    }

    @Inject
    protected ProjectLayout getProjectLayout() {
        throw new UnsupportedOperationException()
    }
}