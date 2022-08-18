package ru.vyarus.gradle.plugin.animalsniffer

import ru.vyarus.gradle.plugin.animalsniffer.report.ReportMessage
import ru.vyarus.gradle.plugin.animalsniffer.util.FormatUtils
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov
 * @since 19.03.2017
 */
class ErrorFormatTest extends Specification {

    def "Check animalsniffer format support"() {

        Set<File> roots = [new File("/opt/foo") ]

        when: "line"
        ReportMessage msg = FormatUtils.parse(
                '/opt/foo/invalid/Sample.java:12 Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])'
                , roots)
        then: "parsed"
        msg.source == 'invalid.Sample.java'
        msg.line == '12'
        msg.code == 'java.nio.file.Path java.nio.file.Paths.get(String, String[])'


        when: "line with space in source"
        msg = FormatUtils.parse(
                '/opt/foo/invalid dir/Sample.java:12 Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])'
                , roots)
        then: "parsed"
        msg.source == 'invalid dir.Sample.java'
        msg.line == '12'
        msg.code == 'java.nio.file.Path java.nio.file.Paths.get(String, String[])'


        when: "line without line num"
        msg = FormatUtils.parse(
                '/opt/foo/invalid/Sample.java Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])'
                , roots)
        then: "parsed"
        msg.source == 'invalid.Sample.java'
        msg.line == null
        msg.code == 'java.nio.file.Path java.nio.file.Paths.get(String, String[])'

    }

    def "Check changed corner phrase"() {

        when: "'undefined reference' not found"
        ReportMessage msg = FormatUtils.parse(
                '/opt/foo/invalid/Sample.java:12 Undefined ref: java.nio.file.Path java.nio.file.Paths.get(String, String[])'
                , [new File('/opt/foo')] as Set)
        then: "parsed"
        msg.source == "invalid.Sample.java"
        msg.line == '12'
        !msg.parseFail
        msg.code == 'Undefined ref: java.nio.file.Path java.nio.file.Paths.get(String, String[])'

        when: "'undefined reference' not found and space too"
        msg = FormatUtils.parse(
                '/opt/foo/invalid/Sample.java:12 Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])'
                , [] as Set)
        then: "parsed"
        msg.source == null
        msg.line == null
        msg.parseFail
        msg.code == '/opt/foo/invalid/Sample.java:12 Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])'

        when: "no source line set"
        msg = FormatUtils.parse(
                '/opt/foo/retrolambda/Sample.java: Undefined reference: java.util.function.Consumer'
                , [new File("/opt/foo") ] as Set)
        then: "parsed"
        msg.source == 'retrolambda.Sample.java'
        msg.line == null
        !msg.parseFail
        msg.code == 'java.util.function.Consumer'
        FormatUtils.formatForConsole(msg, false) == '[Undefined reference] retrolambda.(Sample.java:1)\n  >> java.util.function.Consumer\n'
        FormatUtils.formatForFile(msg, false) == 'retrolambda.Sample  Undefined reference: java.util.function.Consumer'
    }
}