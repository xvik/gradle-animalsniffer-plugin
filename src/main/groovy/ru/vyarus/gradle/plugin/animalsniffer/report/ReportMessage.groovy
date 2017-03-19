package ru.vyarus.gradle.plugin.animalsniffer.report

import groovy.transform.CompileStatic

/**
 * Parsed animalsniffer error.
 *
 * @author Vyacheslav Rusakov
 * @since 19.03.2017
 */
@CompileStatic
class ReportMessage {
    String source
    String line
    String code
    boolean parseFail
}
