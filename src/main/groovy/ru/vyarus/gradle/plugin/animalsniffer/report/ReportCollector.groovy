package ru.vyarus.gradle.plugin.animalsniffer.report

import org.slf4j.Logger

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
class ReportCollector implements InvocationHandler {

    private static final String SPACE = ' '

    // it should be org.gradle.api.internal.project.ant.AntLoggingAdapter
    Object originalListener

    List<String> report = []
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
            append(event.message)
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
            w << report.join('\n')
        }
    }

    /**
     * Writes all violations to console.
     *
     * @param logger build logger
     */
    void writeToConsole(Logger logger) {
        report.each { logger.error "$it" }
        logger.error('')
    }

    private void append(String message) {
        // format: file_path:lineNum: Undefined reference: type sourceline
        String[] res = message.split(SPACE)
        String vclass = res[0]
        String line = vclass.find(~/^(.+):(\d+)/) { match, file, line -> vclass = file; return line }
        vclass = extractJavaClass(vclass)
        affectedFiles.add(vclass)
        String error = res[1..-1].join(SPACE)
        String msg = "$vclass:$line  $error"
        report.add(msg)
    }

    @SuppressWarnings('DuplicateStringLiteral')
    private String extractJavaClass(String file) {
        String name = new File(file).canonicalPath
        roots.each {
            if (name.startsWith(it.canonicalPath)) {
                name = name[it.canonicalPath.length() + 1..-1] // remove sources dir prefix
            }
        }
        name = name[0..name.lastIndexOf('.') - 1] // remove extension
        name.replaceAll('\\\\|/', '.')
    }
}
