package ru.vyarus.gradle.plugin.animalsniffer

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

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

    protected String unifyString(String input) {
        return input
        // cleanup win line break for simpler comparisons
                .replace("\r", '')
    }
}
