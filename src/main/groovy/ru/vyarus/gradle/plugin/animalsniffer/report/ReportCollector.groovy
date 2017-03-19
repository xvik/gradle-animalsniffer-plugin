package ru.vyarus.gradle.plugin.animalsniffer.report

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.slf4j.Logger
import ru.vyarus.gradle.plugin.animalsniffer.util.FormatUtils

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Intercept ant task output and re-format it.
 * Writes result to console and report file.
 * In essence, this is {@link org.apache.tools.ant.BuildListener} proxy implementation.
 *
 * @author Vyacheslav Rusakov
 * @since 16.12.2015
 */
@CompileStatic
class ReportCollector implements InvocationHandler {

    // it should be org.gradle.api.internal.project.ant.AntLoggingAdapter
    Object originalListener

    List<ReportMessage> report = []
    Set<String> affectedFiles = []
    Set<File> roots

    ReportCollector(Set<File> roots) {
        this.roots = roots
    }

    /**
     * Used for ant's BuildListener to intercept logged messages.
     *
     * @param proxy proxy
     * @param method method
     * @param args args
     * @return null
     * @throws Throwable on errors
     */
    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object event = args[0] // org.apache.tools.ant.BuildEvent
        // it should not be possible that other ant task will use this listener, but
        // such case was reported (#3). Use ant task name to filter output
        if (event.task != null && event.task.taskName != 'animalsniffer') {
            if (originalListener) {
                // redirect to original listener
                method.invoke(originalListener, args)
            }
            return null
        }
        if (method.name == 'messageLogged' && event.priority < 2) {
            ReportMessage msg = FormatUtils.parse(event.message, roots)
            affectedFiles.add(msg.source)
            report.add(msg)
        }
        return null
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

    /**
     * Writes detected errors to report file (overriding existing content).
     *
     * @param file report file
     */
    void writeToFile(File file) {
        file.newWriter().withWriter { w ->
            w << report.collect { FormatUtils.formatForFile(it) }.join(String.format('%n'))
        }
    }

    /**
     * Writes all violations to console.
     *
     * @param logger build logger
     */
    void writeToConsole(Logger logger) {
        report.each { logger.error FormatUtils.formatForConsole(it) }
    }
}
