package ru.vyarus.gradle.plugin.animalsniffer

import ru.vyarus.gradle.plugin.animalsniffer.report.ReportMessage
import ru.vyarus.gradle.plugin.animalsniffer.util.FormatUtils
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov
 * @since 19.03.2017
 */
class MsgFormatTest extends Specification {

    def "Check format to file"() {

        when: "format"
        String res = FormatUtils.formatForFile(new ReportMessage(source: "some.Sample.java", line: 2, code: 'bla'))
        then: "correct"
        res == 'some.Sample:2  Undefined reference: bla'

        when: "no line"
        res = FormatUtils.formatForFile(new ReportMessage(source: "some.Sample.java", code: 'bla'))
        then: "correct"
        res == 'some.Sample  Undefined reference: bla'

        when: "incorrect source"
        res = FormatUtils.formatForFile(new ReportMessage(source: "failed source", code: 'bla'))
        then: "correct"
        res == 'failed source  Undefined reference: bla'

        when: "failed parse"
        res = FormatUtils.formatForFile(new ReportMessage(parseFail: true, code: 'bla bla'))
        then: "correct"
        res == 'bla bla'
    }

    def "Check format for console"() {

        when: "format"
        String res = FormatUtils.formatForConsole(new ReportMessage(source: "some.Sample.java", line: 2, code: 'bla'))
        then: "correct"
        res.replaceAll('\r', '') == '[Undefined reference] some.(Sample.java:2)\n  >> bla\n'

        when: "no line"
        res = FormatUtils.formatForConsole(new ReportMessage(source: "some.Sample.java", code: 'bla'))
        then: "correct"
        res.replaceAll('\r', '') == '[Undefined reference] some.(Sample.java:1)\n  >> bla\n'

        when: "incorrect source"
        res = FormatUtils.formatForConsole(new ReportMessage(source: "failed source", code: 'bla'))
        then: "correct"
        res.replaceAll('\r', '') == '[Undefined reference] failed source\n  >> bla\n'

        when: "failed parse"
        res = FormatUtils.formatForConsole(new ReportMessage(parseFail: true, code: 'bla bla'))
        then: "correct"
        res.replaceAll('\r', '') == 'bla bla'
    }
}