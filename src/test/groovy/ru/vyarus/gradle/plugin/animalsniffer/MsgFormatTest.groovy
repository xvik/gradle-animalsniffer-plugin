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
        def message = new ReportMessage(source: "some.Sample.java", line: 2, code: 'bla', signature: 'java16-sun-1.0')
        String res = FormatUtils.formatForFile(message, false)
        then: "correct"
        res == 'some.Sample:2  Undefined reference: bla'

        when: "format with signature"
        res = FormatUtils.formatForFile(message, true)
        then: "correct"
        res == 'some.Sample:2  Undefined reference (java16-sun-1.0): bla'

        when: "no line"
        res = FormatUtils.formatForFile(new ReportMessage(source: "some.Sample.java", code: 'bla'), false)
        then: "correct"
        res == 'some.Sample:1  Undefined reference: bla'

        when: "incorrect source"
        res = FormatUtils.formatForFile(new ReportMessage(source: "failed source", code: 'bla'), false)
        then: "correct"
        res == 'failed source:1  Undefined reference: bla'

        when: "failed parse"
        res = FormatUtils.formatForFile(new ReportMessage(parseFail: true, code: 'bla bla'), false)
        then: "correct"
        res == 'bla bla'
    }

    def "Check format for console"() {

        when: "format"
        def message = new ReportMessage(source: "some.Sample.java", line: 2, code: 'bla', signature: 'java16-sun-1.0')
        String res = FormatUtils.formatForConsole(message, false)
        then: "correct"
        res.replaceAll('\r', '') == '[Undefined reference] some.(Sample.java:2)\n  >> bla\n'

        when: "format with signature"
        res = FormatUtils.formatForConsole(message, true)
        then: "correct"
        res.replaceAll('\r', '') == '[Undefined reference | java16-sun-1.0] some.(Sample.java:2)\n  >> bla\n'

        when: "no line"
        res = FormatUtils.formatForConsole(new ReportMessage(source: "some.Sample.java", code: 'bla'), false)
        then: "correct"
        res.replaceAll('\r', '') == '[Undefined reference] some.(Sample.java:1)\n  >> bla\n'

        when: "incorrect source"
        res = FormatUtils.formatForConsole(new ReportMessage(source: "failed source", code: 'bla'), false)
        then: "correct"
        res.replaceAll('\r', '') == '[Undefined reference] failed source\n  >> bla\n'

        when: "failed parse"
        res = FormatUtils.formatForConsole(new ReportMessage(parseFail: true, code: 'bla bla'), false)
        then: "correct"
        res.replaceAll('\r', '') == '[Unrecognized error] bla bla \n'
    }
}