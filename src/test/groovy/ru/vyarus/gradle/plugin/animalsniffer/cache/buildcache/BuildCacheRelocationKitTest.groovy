package ru.vyarus.gradle.plugin.animalsniffer.cache.buildcache

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest
import spock.lang.TempDir

/**
 * Relocation is a CI case when project is checked out always into new directory, but cache still must be
 * taken into account (for speed up).
 *
 * @author Vyacheslav Rusakov
 * @since 27.08.2018
 */
class BuildCacheRelocationKitTest extends AbstractKitTest {

    @TempDir
    File cacheDir

    @TempDir
    File relocatedDir

    def "Check build cache support"() {
        setup:
        // build cache will survive within test only!!
        file("settings.gradle") << """
            buildCache {
                local(DirectoryBuildCache) {
                    directory = new File('${cacheDir.canonicalPath.replace('\\', '\\\\')}')
                }
            }
"""
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
            }

            repositories { mavenCentral() }
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }
        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        // create complete project copy
        new AntBuilder().copy(todir: relocatedDir) {
            fileset(dir: testProjectDir)
        }

        when: "run task"
        BuildResult result = run('check', '--build-cache')

        then: "tasks executes"
        result.task(':compileJava').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.output.contains("2 AnimalSniffer violations were found in 1 files")



        when: "run other project from cache"
        result = gradle(relocatedDir, 'check', '--build-cache').build()

        then: "tasks cached"
        result.task(':compileJava').outcome == TaskOutcome.FROM_CACHE
        result.task(':animalsnifferMain').outcome == TaskOutcome.FROM_CACHE

        then: "no console output for cached task!"
        !result.output.contains("2 AnimalSniffer violations were found in 1 files")


        then: "report correct"
        File file = new File(relocatedDir, '/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:11  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:16  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }


    def "Check build cache with signature caching enabled"() {
        setup:
        // build cache will survive within test only!!
        file("settings.gradle") << """
            buildCache {
                local(DirectoryBuildCache) {
                    directory = new File('${cacheDir.canonicalPath.replace('\\', '\\\\')}')
                }
            }
"""
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                ignoreFailures = true
                cache.enabled = true
            }

            repositories { mavenCentral() }
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }
        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        // create complete project copy
        new AntBuilder().copy(todir: relocatedDir) {
            fileset(dir: testProjectDir)
        }


        when: "run task"
        BuildResult result = run('check', '--build-cache')

        then: "tasks executes"
        result.task(':compileJava').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferCacheMain').outcome == TaskOutcome.SUCCESS
        result.task(':animalsnifferMain').outcome == TaskOutcome.SUCCESS

        then: "found 2 violations"
        result.output.contains("2 AnimalSniffer violations were found in 1 files")


        when: "run other project from cache"
        result = gradle(relocatedDir, 'check', '--build-cache').build()

        then: "tasks cached"
        result.task(':compileJava').outcome == TaskOutcome.FROM_CACHE
        result.task(':animalsnifferCacheMain').outcome == TaskOutcome.FROM_CACHE
        result.task(':animalsnifferMain').outcome == TaskOutcome.FROM_CACHE

        then: "no console output for cached task!"
        !result.output.contains("2 AnimalSniffer violations were found in 1 files")


        then: "report correct"
        File file = new File(relocatedDir,'/build/reports/animalsniffer/main.text')
        file.exists()
        file.readLines() == [
                "invalid.Sample:11  Undefined reference: int Boolean.compare(boolean, boolean)",
                "invalid.Sample:16  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])"
        ]
    }
}
