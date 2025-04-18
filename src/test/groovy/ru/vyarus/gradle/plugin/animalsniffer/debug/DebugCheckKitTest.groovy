package ru.vyarus.gradle.plugin.animalsniffer.debug

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import ru.vyarus.gradle.plugin.animalsniffer.AbstractKitTest

/**
 * @author Vyacheslav Rusakov
 * @since 08.02.2023
 */
class DebugCheckKitTest extends AbstractKitTest {

    def "Check plugin execution"() {

        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                debug = true
                ignoreFailures = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }
        """

        fileFromClasspath('src/main/java/valid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
        //debug()

        when: "run task"
        BuildResult result = run('printAnimalsnifferTasks', 'check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        and: "contains tasks list"
        def out = clean(result.output)
        out.contains(""":printAnimalsnifferTasks


\tanimalsnifferMain                   [default]       for 'main' source set
\t\treport: build/reports/animalsniffer/main.text
\t\tdepends on: classes
\t\tsignatures: 
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/java/main
\t\tsources:
\t\t\tsrc/main/java


\tanimalsnifferTest                                   for 'test' source set
\t\treport: build/reports/animalsniffer/test.text
\t\tdepends on: testClasses
\t\tsignatures: 
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/java/test
\t\tsources:
\t\t\tsrc/test/java                                                                    NOT EXISTS

*use [printAnimalsnifferSourceInfo] task to see project sources configuration details
""")

        and: "contains task info"
        out.contains """
\tsignatures:
\t\tjava16-sun-1.0.signature

\tsources:
\t\tsrc/main/java

\tfiles:
\t\tbuild/classes/java/main/valid/Sample.class
"""
    }


    def "Check multiple signatures"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                debug = true
                ignoreFailures = true
            }

            repositories { mavenCentral() }
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
            }
        """
        fileFromClasspath('src/main/java/android/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/android/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('printAnimalsnifferTasks', 'check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        and: "contains tasks list"
        def out = clean(result.output)
        out.contains(""":printAnimalsnifferTasks


\tanimalsnifferMain                   [default]       for 'main' source set
\t\treport: build/reports/animalsniffer/main.text
\t\tdepends on: classes
\t\tsignatures: 
\t\t\tandroid-api-level-14-4.0_r4.signature
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/java/main
\t\tsources:
\t\t\tsrc/main/java


\tanimalsnifferTest                                   for 'test' source set
\t\treport: build/reports/animalsniffer/test.text
\t\tdepends on: testClasses
\t\tsignatures: 
\t\t\tandroid-api-level-14-4.0_r4.signature
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/java/test
\t\tsources:
\t\t\tsrc/test/java                                                                    NOT EXISTS

*use [printAnimalsnifferSourceInfo] task to see project sources configuration details
""")

        and: "correct task info"
        out.contains """
\tsignatures:
\t\tjava16-sun-1.0.signature
\t\tandroid-api-level-14-4.0_r4.signature

\tsources:
\t\tsrc/main/java

\tfiles:
\t\tbuild/classes/java/main/android/Sample.class
"""
    }

    def "Check execution with ignored classes"() {

        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                debug = true
                ignoreFailures = true
                ignore 'java.nio.file.Paths'
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }
        """

        fileFromClasspath('src/main/java/valid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/valid/Sample.java')
        //debug()

        when: "run task"
        BuildResult result = run('printAnimalsnifferTasks', 'check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        and: "contain tasks list"
        def out = clean(result.output)
        out.contains(""":printAnimalsnifferTasks


\tanimalsnifferMain                   [default]       for 'main' source set
\t\treport: build/reports/animalsniffer/main.text
\t\tdepends on: classes
\t\tsignatures: 
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/java/main
\t\tsources:
\t\t\tsrc/main/java


\tanimalsnifferTest                                   for 'test' source set
\t\treport: build/reports/animalsniffer/test.text
\t\tdepends on: testClasses
\t\tsignatures: 
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/java/test
\t\tsources:
\t\t\tsrc/test/java                                                                    NOT EXISTS

*use [printAnimalsnifferSourceInfo] task to see project sources configuration details
""")

        and: "task info"
        out.contains """
\tsignatures:
\t\tjava16-sun-1.0.signature

\tsources:
\t\tsrc/main/java

\tfiles:
\t\tbuild/classes/java/main/valid/Sample.class

\tignored:
\t\tjava.nio.file.Paths
"""
    }


    def "Check cache debug enabling"() {
        setup:
        build """
            plugins {
                id 'java'
                id 'ru.vyarus.animalsniffer'
            }

            animalsniffer {
                debug = true
                ignoreFailures = true
                cache.enabled = true
            }

            repositories { mavenCentral()}
            dependencies {
                signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
                implementation 'org.slf4j:slf4j-api:1.7.25'
            }

        """
        fileFromClasspath('src/main/java/invalid/Sample.java', '/ru/vyarus/gradle/plugin/animalsniffer/java/invalid/Sample.java')
//        debug()

        when: "run task"
        BuildResult result = run('printAnimalsnifferTasks', 'check')

        then: "task successful"
        result.task(':check').outcome == TaskOutcome.SUCCESS

        and: "contain tasks list"
        def out = clean(result.output)
        out.contains(""":printAnimalsnifferTasks


\tanimalsnifferMain                   [default]       for 'main' source set
\t\treport: build/reports/animalsniffer/main.text
\t\tdepends on: cacheAnimalsnifferMainSignatures, classes
\t\tsignatures: (cached signature)
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/java/main
\t\tsources:
\t\t\tsrc/main/java


\tanimalsnifferTest                                   for 'test' source set
\t\treport: build/reports/animalsniffer/test.text
\t\tdepends on: cacheAnimalsnifferTestSignatures, testClasses
\t\tsignatures: (cached signature)
\t\t\tjava16-sun-1.0.signature
\t\tclasses:
\t\t\tbuild/classes/java/test
\t\tsources:
\t\t\tsrc/test/java                                                                    NOT EXISTS

*use [printAnimalsnifferSourceInfo] task to see project sources configuration details
""")

        and: "task info"
        out.contains """animalsnifferMainCache.sig

\tsignatures:
\t\tjava16-sun-1.0.signature

\tfiles:"""
        out.contains """caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.25/da76ca59f6a57ee3102f8f9bd9cee742973efa8a/slf4j-api-1.7.25.jar

\texclude:
\t\tsun.*
\t\torg.gradle.internal.impldep.*
"""

        out.contains """
\tsignatures:
\t\tanimalsnifferMainCache.sig

\tsources:
\t\tsrc/main/java

\tfiles:
\t\tbuild/classes/java/main/invalid/Sample.class
"""
    }

    private clean(String out) {
        return out.replace('\r', '').replace(File.separator, '/')
    }
}
