package ru.vyarus.gradle.plugin.animalsniffer.util

import ru.vyarus.gradle.plugin.animalsniffer.report.ReportMessage

/**
 * Message parsing utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 19.03.2017
 */
class FormatUtils {
    private static final String DOT = '.'
    private static final String NL = String.format('%n')
    private static final String UND_REF = 'Undefined reference:'

    /**
     * Parse animasniffer ant task error.
     * Error format: <code>file_path:lineNum: Undefined reference: type source line</code>
     *
     * @param message animalsniffer error
     * @param roots source directories (roots)
     * @return report message object
     */
    static ReportMessage parse(String message, Set<File> roots) {
        int msgStartIdx = message.indexOf(UND_REF)
        if (msgStartIdx < 0) {
            // try to look for first space (this will lead to wrong result if file path contain space)
            msgStartIdx = message.indexOf(' ')
        }
        String vclass = message[0..(msgStartIdx - 1)].trim()
        String line = vclass.find(~/^(.+):(\d+)/) { match, file, line -> vclass = file; return line }
        vclass = extractJavaClass(vclass, roots)
        if (vclass == null) {
            return new ReportMessage(parseFail: true, code: message)
        }
        String code = message[msgStartIdx..-1]
        code = code.replace(UND_REF, '').trim()
        return new ReportMessage(source: vclass, line: line, code: code)
    }

    /**
     * @param msg message object
     * @return error message formatted for file
     */
    static String formatForFile(ReportMessage msg) {
        if (msg.parseFail) {
            return msg.code
        }
        // file extension position
        int idx = msg.source.lastIndexOf(DOT)
        if (idx < 0) {
            idx = 0
        }
        String srcLine = msg.line ?
                "${msg.source[0..(idx - 1)]}:${msg.line}" :
                "${msg.source[0..(idx - 1)]}"
        return "$srcLine  Undefined reference: ${msg.code}"
    }

    /**
     *
     * @param msg message object
     * @return error message formatted for console
     */
    static String formatForConsole(ReportMessage msg) {
        if (msg.parseFail) {
            return "[Unrecognized error] ${msg.code} $NL"
        }
        int clsIdx = -1
        int extIdx = msg.source.lastIndexOf(DOT)
        if (extIdx > 0) {
            clsIdx = msg.source[0..(extIdx - 1)].lastIndexOf(DOT)
        }
        // if can't find class fallback to simple format
        String srcLine = clsIdx > 0 ?
                "${msg.source[0..clsIdx]}(${msg.source[(clsIdx + 1)..-1]}:${msg.line ?: 1})" :
                "${msg.source}${msg.line ? ':' + msg.line : ''}"
        return "[Undefined reference] $srcLine$NL" +
                "  >> ${msg.code}$NL"
    }

    /**
     *
     * @param file absolute file path
     * @param root root folder path
     * @return class name (including package)
     */
    static String toClass(String file, String root) {
        String name = file[root.length() + 1..-1]
        name.replaceAll('\\\\|/', DOT)
    }

    @SuppressWarnings('ReturnNullFromCatchBlock')
    private static String extractJavaClass(String file, Set<File> roots) {
        String name
        try {
            name = new File(file).canonicalPath
        } catch (IOException ex) {
            // invalid path - do nothing
            return null
        }
        File root = roots.find { name.startsWith(it.canonicalPath) }
        if (!root) {
            return null
        }
        toClass(name, root.canonicalPath)
    }
}
