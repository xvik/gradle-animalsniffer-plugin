package ru.vyarus.gradle.plugin.animalsniffer

import com.github.difflib.text.DiffRow
import com.github.difflib.text.DiffRowGenerator
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import java.util.function.Function

/**
 * Base class for Gradle TestKit based tests.
 * Useful for full-cycle and files manipulation testing.
 *
 * @author Vyacheslav Rusakov
 * @since 13.12.2015
 */
abstract class AbstractKitTest extends Specification {

    boolean debug
    @TempDir File testProjectDir
    File buildFile
    boolean isWin = Os.isFamily(Os.FAMILY_WINDOWS)

    def setup() {
        buildFile = file('build.gradle')
        // jacoco coverage support
        fileFromClasspath('gradle.properties', 'testkit-gradle.properties')
    }

    def build(String file) {
        buildFile << file
    }

    File file(String path) {
        File res = new File(testProjectDir, path)
        res.parentFile.mkdirs()
        res
    }

    File fileFromClasspath(String toFile, String source) {
        File target = file(toFile)
        target.parentFile.mkdirs()
        target.withOutputStream {
            it.write((getClass().getResourceAsStream(source) ?: getClass().classLoader.getResourceAsStream(source)).bytes)
        }
        target
    }

    String readFileFromClasspath(String source) {
        def stream = getClass().getResourceAsStream(source) ?: getClass().classLoader.getResourceAsStream(source)
        if (stream == null) {
            throw new FileNotFoundException("Classpath file not found: $source")
        }
        stream.text
    }

    /**
     * Enable it and run test with debugger (no manual attach required). Not always enabled to speed up tests during
     * normal execution.
     */
    def debug() {
        debug = true
    }

    String projectName() {
        return testProjectDir.getName()
    }

    GradleRunner gradle(File root, String... commands) {
        applyCommonConfiguration(GradleRunner.create()
                .withProjectDir(root)
                .withArguments((commands + ['--stacktrace']) as String[])
                .withPluginClasspath()
                .withDebug(debug)
                .forwardOutput())
    }

    GradleRunner gradle(String... commands) {
        gradle(testProjectDir, commands)
    }

    BuildResult run(String... commands) {
        return gradle(commands).build()
    }

    BuildResult runFailed(String... commands) {
        return gradle(commands).buildAndFail()
    }

    BuildResult runVer(String gradleVersion, String... commands) {
        return gradle(commands).withGradleVersion(gradleVersion).build()
    }

    BuildResult runFailedVer(String gradleVersion, String... commands) {
        return gradle(commands).withGradleVersion(gradleVersion).buildAndFail()
    }

    GradleRunner applyCommonConfiguration(GradleRunner runner) {
        // do nothing, but could be used in tests to additionally configure (like apply environment)
        runner
    }

    public static final String RESET = '\u001B[0m'
    public static final String RED = '\u001B[31m'
    public static final String GREEN = '\u001B[32m'
    public static final String YELLOW = '\u001B[33m'
    public static final String BLUE = '\u001B[34m'
    public static final String PURPLE = '\u001B[35m'
    public static final String CYAN = '\u001B[36m'

    private static final String NL = '\n'

    protected boolean equalWithDiff(String expected, String actual) {
        List<String> original = Arrays.asList(expected.split(NL))
        List<String> revised = Arrays.asList(actual.split(NL))

        final String plus = '+'
        final String minus = '-'

        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .mergeOriginalRevised(true)
                .ignoreWhiteSpaces(true)
                .inlineDiffByWord(true)
                .lineNormalizer { it }  // to disable html escapes
                .equalizer(DiffRowGenerator.IGNORE_WHITESPACE_EQUALIZER)
                .oldTag({ it ? minus + RED : RESET + minus } as Function<Boolean, String>)
                .newTag({ it ? plus + GREEN : RESET + plus } as Function<Boolean, String>)
                .build()
        List<DiffRow> rows = generator.generateDiffRows(original, revised)
        List<Integer> showRows = []
        int last = -1
        for (int i = 0; i < rows.size(); i++) {
            DiffRow row = rows.get(i)
            if (DiffRow.Tag.EQUAL != row.tag) {
                // show 2 rows before
                int prev = Math.max(last + 1, i - 2)
                for (int j = prev; j <= i; j++) {
                    showRows.add(j)
                }
                last = i
            }
        }

        StringBuilder res = new StringBuilder()
        int prev = -1
        for (int i : showRows) {
            DiffRow row = rows.get(i)
            if (prev > 0 && prev != i - 1) {
                // between blocks
                res.append(NL)
            }
            res.append(String.format('%4s | ', i)).append(row.oldLine).append(NL)
            prev = i
        }

        if (res.size() > 0) {
            println "\n!!!!!!!!! Strings difference: \n\n" + res.toString()
        }

        return res.size() == 0
    }

    protected String unifyStringLinSlashes(String input) {
        return unifyString(input).replace('\\', '/')
    }

    protected String unifyString(String input) {
        return input
        // cleanup win line break for simpler comparisons
                .replace("\r", '')
    }
}
