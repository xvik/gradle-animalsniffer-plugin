package ru.vyarus.gradle.plugin.animalsniffer.report

import groovy.transform.CompileStatic

/**
 * Animalsniffer is executed inside worker (potentially, different jvm) so the only way to get access to discovered
 * errors is to use an external file. All errors stored in a simple CSV form. File could be empty in cases when
 * something goes wrong in check process.
 *
 * @author Vyacheslav Rusakov
 * @since 10.01.2025
 */
@CompileStatic
class ReportParser {

    private static final String SEPARATOR = ';'

    /**
     * @param msg error messages
     * @return error messages as csv text
     */
    @SuppressWarnings('Instanceof')
    static String toCsv(ReportMessage msg) {
        return [msg.signature, msg.source, msg.line, msg.field, msg.code, msg.parseFail]
                .collect {
                    if (it instanceof Boolean) {
                        return it.toString()
                    }
                    return it ? it.toString() : ''
                }.join(SEPARATOR)
    }

    /**
     * @param line single csv line
     * @return parsed error object
     */
    static ReportMessage fromCsv(String line) {
        if (line) {
            String[] split = line.split(SEPARATOR)
            return new ReportMessage(
                    signature: split[0],
                    source: split[1],
                    line: split[2],
                    field: split[3],
                    code: split[4],
                    parseFail: Boolean.parseBoolean(split[5])
            )
        }
        return null
    }
}
