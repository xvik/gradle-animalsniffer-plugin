# Check kotlin multiplatform

For kotlin multiplatform project (`org.jetbrains.kotlin.multiplatform` plugin version 1.7.0 or above)
animalsniffer tasks created for **platform compilations**, except android platform (see below) and
metadata platform (because it's sources always included in other platforms).

## Examples

To better understand how multipltaform sources organized, lets look into 3 cases:

1. Only one platform declared (the simplest)
2. Multiple platforms declared
3. Android platform declared (android plugin also enabled)

###  Single platform example

```groovy
plugins {
    id 'org.jetbrains.kotlin.multiplatform' version '2.0.21'
    id 'ru.vyarus.animalsniffer' version "{{ gradle.version }}"
}

kotlin {
    jvm()
}

dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
}
```

!!! note
    It is *not required* to enable `.withJava()` (was required in the previous plugin versions)

Part of [print sources task](../debug/sources.md) output:
```
== [SourceSets] ===============================================================

	Kotlin Multiplatform Source Sets ------------------------------------------------------- (4)

		commonMain ----- (consumed by compilation 'main' (target metadata (common)))
			src/commonMain/kotlin                                                            NOT EXISTS

		commonTest ----- (consumed by compilation 'test' (target jvm (jvm)))
			src/commonTest/kotlin                                                            NOT EXISTS

		jvmMain ----- (consumed by compilation 'main' (target jvm (jvm)))
			src/jvmMain/kotlin

		jvmTest ----- (consumed by compilation 'test' (target jvm (jvm)))
			src/jvmTest/kotlin                                                               NOT EXISTS

== [Kotlin targets] ========================================================= (2)

	jvm ----- (jvm platform, 2 compilations)

		compilation 'main' (target jvm (jvm)) (compiled by jvmMainClasses task)

			Source sets (2)

				jvmMain -----
					src/jvmMain/kotlin

				commonMain -----
					src/commonMain/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/jvm/main

		compilation 'test' (target jvm (jvm)) (compiled by jvmTestClasses task)

			Source sets (2)

				jvmTest -----
					src/jvmTest/kotlin                                                               NOT EXISTS

				commonTest -----
					src/commonTest/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/jvm/test

			Associated compilations (1)
				main

	metadata ----- (common platform, 1 compilations)

		compilation 'main' (target metadata (common)) (compiled by metadataMainClasses task)

			Source sets (1)

				commonMain -----
					src/commonMain/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/metadata/main

```

Only 2 animalsniffer tasks would be created for jvm platform: `animalsnifferJvmMain` and `animalsnifferJvmTest`
('common' platform sources are already included into jvm platform).

This could also be seen with [print animalsniffer tasks](../debug/tasks.md) task:

```
> Task :printAnimalsnifferTasks


	animalsnifferJvmMain                [default]       for kotlin platform 'jvm' compilation 'main'
		report: build/reports/animalsniffer/jvmMain.text
		depends on: jvmMainClasses
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/classes/kotlin/jvm/main
		sources:
			src/commonMain/kotlin                                                            NOT EXISTS
			src/jvmMain/kotlin


	animalsnifferJvmTest                                for kotlin platform 'jvm' compilation 'test'
		report: build/reports/animalsniffer/jvmTest.text
		depends on: jvmTestClasses
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/classes/kotlin/jvm/test
		sources:
			src/commonTest/kotlin                                                            NOT EXISTS
			src/jvmTest/kotlin                                                               NOT EXISTS
```

!!! note
    `[default]` indicates tasks executed with build

!!! tip
    Use [`animalsniffer.debug = true`](../debug/debug.md) to see class files, checked by animalsniffer task


### Multiple platforms example:

```groovy
plugins {
    id 'org.jetbrains.kotlin.multiplatform' version '2.0.21'
    id 'ru.vyarus.animalsniffer' version "{{ gradle.version }}"
}

kotlin {
    jvm()
    
    js {
        browser()
    }
}

dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
}
```

Part of [print sources task](../debug/sources.md) output:
```
== [SourceSets] ===============================================================

	Kotlin Multiplatform Source Sets ------------------------------------------------------- (6)

		commonMain ----- (consumed by compilation 'main' (target metadata (common)))
			src/commonMain/kotlin                                                            NOT EXISTS

		commonTest ----- (consumed by compilation 'test' (target jvm (jvm)))
			src/commonTest/kotlin                                                            NOT EXISTS

		jsMain ----- (consumed by compilation 'main' (target js (js)))
			src/jsMain/kotlin                                                                NOT EXISTS

		jsTest ----- (consumed by compilation 'test' (target js (js)))
			src/jsTest/kotlin                                                                NOT EXISTS

		jvmMain ----- (consumed by compilation 'main' (target jvm (jvm)))
			src/jvmMain/kotlin

		jvmTest ----- (consumed by compilation 'test' (target jvm (jvm)))
			src/jvmTest/kotlin                                                               NOT EXISTS

== [Kotlin targets] ========================================================= (3)

	js ----- (js platform, 2 compilations)

		compilation 'main' (target js (js)) (compiled by jsMainClasses task)

			Source sets (2)

				jsMain -----
					src/jsMain/kotlin                                                                NOT EXISTS

				commonMain -----
					src/commonMain/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/js/main

		compilation 'test' (target js (js)) (compiled by jsTestClasses task)

			Source sets (2)

				jsTest -----
					src/jsTest/kotlin                                                                NOT EXISTS

				commonTest -----
					src/commonTest/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/js/test

			Associated compilations (1)
				main

	jvm ----- (jvm platform, 2 compilations)

		compilation 'main' (target jvm (jvm)) (compiled by jvmMainClasses task)

			Source sets (2)

				jvmMain -----
					src/jvmMain/kotlin

				commonMain -----
					src/commonMain/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/jvm/main

		compilation 'test' (target jvm (jvm)) (compiled by jvmTestClasses task)

			Source sets (2)

				jvmTest -----
					src/jvmTest/kotlin                                                               NOT EXISTS

				commonTest -----
					src/commonTest/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/jvm/test

			Associated compilations (1)
				main

	metadata ----- (common platform, 2 compilations)

		compilation 'commonMain' (target metadata (common)) (compiled by metadataCommonMainClasses task)

			Source sets (1)

				commonMain -----
					src/commonMain/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/metadata/commonMain

		compilation 'main' (target metadata (common)) (compiled by metadataMainClasses task)

			Source sets (1)

				commonMain -----
					src/commonMain/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/metadata/main
```


[Print animalsniffer tasks](../debug/tasks.md) task:
```
> Task :printAnimalsnifferTasks


	animalsnifferJsMain                 [default]       for kotlin platform 'js' compilation 'main'
		report: build/reports/animalsniffer/jsMain.text
		depends on: jsMainClasses
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/classes/kotlin/js/main
		sources:
			src/commonMain/kotlin                                                            NOT EXISTS
			src/jsMain/kotlin                                                                NOT EXISTS


	animalsnifferJsTest                                 for kotlin platform 'js' compilation 'test'
		report: build/reports/animalsniffer/jsTest.text
		depends on: jsTestClasses
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/classes/kotlin/js/test
		sources:
			src/commonTest/kotlin                                                            NOT EXISTS
			src/jsTest/kotlin                                                                NOT EXISTS


	animalsnifferJvmMain                [default]       for kotlin platform 'jvm' compilation 'main'
		report: build/reports/animalsniffer/jvmMain.text
		depends on: jvmMainClasses
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/classes/kotlin/jvm/main
		sources:
			src/commonMain/kotlin                                                            NOT EXISTS
			src/jvmMain/kotlin


	animalsnifferJvmTest                                for kotlin platform 'jvm' compilation 'test'
		report: build/reports/animalsniffer/jvmTest.text
		depends on: jvmTestClasses
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/classes/kotlin/jvm/test
		sources:
			src/commonTest/kotlin                                                            NOT EXISTS
			src/jvmTest/kotlin                                                               NOT EXISTS
```

### Android platform example

Kotlin multiplatform support does not create tasks for android platform: android plugin support used instead
(with just a few missed kotlin sources added to animalsniffer tasks).

```groovy
plugins {
    id 'org.jetbrains.kotlin.multiplatform' version '2.0.21'
    id 'com.android.application' version '8.4.0'                
    id 'ru.vyarus.animalsniffer' version "{{ gradle.version }}"
}

kotlin {
    androidTarget()
    
    android { ... }
}

dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
}
```


Part of [print sources task](../debug/sources.md) output:
```
== [SourceSets] ===============================================================

	Kotlin Multiplatform Source Sets ------------------------------------------------------- (10)

		androidDebug ----- (consumed by compilation 'debug' (target android (androidJvm)))
			src/androidDebug/kotlin                                                          NOT EXISTS
			src/debug/java                                                                   NOT EXISTS
			src/debug/kotlin                                                                 NOT EXISTS

		androidInstrumentedTest ----- (consumed by compilation 'debugAndroidTest' (target android (androidJvm)))
			src/androidInstrumentedTest/kotlin                                               NOT EXISTS
			src/androidTest/java                                                             NOT EXISTS
			src/androidTest/kotlin                                                           NOT EXISTS

		androidInstrumentedTestDebug ----- (consumed by compilation 'debugAndroidTest' (target android (androidJvm)))
			src/androidInstrumentedTestDebug/kotlin                                          NOT EXISTS
			src/androidTestDebug/java                                                        NOT EXISTS
			src/androidTestDebug/kotlin                                                      NOT EXISTS

		androidMain ----- (consumed by compilation 'release' (target android (androidJvm)))
			src/androidMain/kotlin
			src/main/java                                                                    NOT EXISTS
			src/main/kotlin                                                                  NOT EXISTS

		androidRelease ----- (consumed by compilation 'release' (target android (androidJvm)))
			src/androidRelease/kotlin                                                        NOT EXISTS
			src/release/java                                                                 NOT EXISTS
			src/release/kotlin                                                               NOT EXISTS

		androidUnitTest ----- (consumed by compilation 'releaseUnitTest' (target android (androidJvm)))
			src/androidUnitTest/kotlin                                                       NOT EXISTS
			src/test/java                                                                    NOT EXISTS
			src/test/kotlin                                                                  NOT EXISTS

		androidUnitTestDebug ----- (consumed by compilation 'debugUnitTest' (target android (androidJvm)))
			src/androidUnitTestDebug/kotlin                                                  NOT EXISTS
			src/testDebug/java                                                               NOT EXISTS
			src/testDebug/kotlin                                                             NOT EXISTS

		androidUnitTestRelease ----- (consumed by compilation 'releaseUnitTest' (target android (androidJvm)))
			src/androidUnitTestRelease/kotlin                                                NOT EXISTS
			src/testRelease/java                                                             NOT EXISTS
			src/testRelease/kotlin                                                           NOT EXISTS

		commonMain ----- (consumed by compilation 'main' (target metadata (common)))
			src/commonMain/kotlin                                                            NOT EXISTS

		commonTest ----- (consumed by compilation 'releaseUnitTest' (target android (androidJvm)))
			src/commonTest/kotlin                                                            NOT EXISTS

	Android sources NOT COVERED by kotlin source sets
		src/androidTestRelease/java                                            (androidTestRelease)
		src/androidTestRelease/kotlin                                          (androidTestRelease)
		src/testFixtures/java                                                  (testFixtures)
		src/testFixtures/kotlin                                                (testFixtures)
		src/testFixturesDebug/java                                             (testFixturesDebug)
		src/testFixturesDebug/kotlin                                           (testFixturesDebug)
		src/testFixturesRelease/java                                           (testFixturesRelease)
		src/testFixturesRelease/kotlin                                         (testFixturesRelease)

	Android application Source Sets ------------------------------------------------------ (12)

		androidTest ----- (consumed by test component debugAndroidTest)
			src/androidInstrumentedTest/kotlin                                               NOT EXISTS
			src/androidTest/java                                                             NOT EXISTS
			src/androidTest/kotlin                                                           NOT EXISTS

		androidTestDebug ----- (consumed by test component debugAndroidTest)
			src/androidInstrumentedTestDebug/kotlin                                          NOT EXISTS
			src/androidTestDebug/java                                                        NOT EXISTS
			src/androidTestDebug/kotlin                                                      NOT EXISTS

		androidTestRelease -----
			src/androidTestRelease/java                                                      NOT EXISTS
			src/androidTestRelease/kotlin                                                    NOT EXISTS

		debug ----- (consumed by variant debug)
			src/androidDebug/kotlin                                                          NOT EXISTS
			src/debug/java                                                                   NOT EXISTS
			src/debug/kotlin                                                                 NOT EXISTS

		main ----- (consumed by variant release)

			Sources
				src/androidMain/kotlin
				src/main/java                                                                    NOT EXISTS
				src/main/kotlin                                                                  NOT EXISTS

			Classpath
				slf4j-api-1.7.25.jar

		release ----- (consumed by variant release)
			src/androidRelease/kotlin                                                        NOT EXISTS
			src/release/java                                                                 NOT EXISTS
			src/release/kotlin                                                               NOT EXISTS

		test ----- (consumed by unit test component releaseUnitTest)
			src/androidUnitTest/kotlin                                                       NOT EXISTS
			src/test/java                                                                    NOT EXISTS
			src/test/kotlin                                                                  NOT EXISTS

		testDebug ----- (consumed by unit test component debugUnitTest)
			src/androidUnitTestDebug/kotlin                                                  NOT EXISTS
			src/testDebug/java                                                               NOT EXISTS
			src/testDebug/kotlin                                                             NOT EXISTS

		testFixtures -----
			src/testFixtures/java                                                            NOT EXISTS
			src/testFixtures/kotlin                                                          NOT EXISTS

		testFixturesDebug -----
			src/testFixturesDebug/java                                                       NOT EXISTS
			src/testFixturesDebug/kotlin                                                     NOT EXISTS

		testFixturesRelease -----
			src/testFixturesRelease/java                                                     NOT EXISTS
			src/testFixturesRelease/kotlin                                                   NOT EXISTS

		testRelease ----- (consumed by unit test component releaseUnitTest)
			src/androidUnitTestRelease/kotlin                                                NOT EXISTS
			src/testRelease/java                                                             NOT EXISTS
			src/testRelease/kotlin                                                           NOT EXISTS

	Kotlin sources NOT COVERED by android source sets
		src/commonMain/kotlin                                                  (commonMain)
		src/commonTest/kotlin                                                  (commonTest)

== [Kotlin targets] ========================================================= (2)

	android ----- (androidJvm platform, 5 compilations)

		compilation 'debug' (target android (androidJvm)) (compiled by androidDebugClasses task)

			Source sets (3)

				androidDebug -----
					src/androidDebug/kotlin                                                          NOT EXISTS
					src/debug/java                                                                   NOT EXISTS
					src/debug/kotlin                                                                 NOT EXISTS

				androidMain -----
					src/androidMain/kotlin
					src/main/java                                                                    NOT EXISTS
					src/main/kotlin                                                                  NOT EXISTS

				commonMain -----
					src/commonMain/kotlin                                                            NOT EXISTS

			Output
				build/intermediates/javac/debug/compileDebugJavaWithJavac/classes
				build/tmp/kotlin-classes/debug

		compilation 'debugAndroidTest' (target android (androidJvm)) (compiled by androidDebugAndroidTestClasses task)

			Source sets (2)

				androidInstrumentedTestDebug -----
					src/androidInstrumentedTestDebug/kotlin                                          NOT EXISTS
					src/androidTestDebug/java                                                        NOT EXISTS
					src/androidTestDebug/kotlin                                                      NOT EXISTS

				androidInstrumentedTest -----
					src/androidInstrumentedTest/kotlin                                               NOT EXISTS
					src/androidTest/java                                                             NOT EXISTS
					src/androidTest/kotlin                                                           NOT EXISTS

			Output
				build/intermediates/javac/debugAndroidTest/compileDebugAndroidTestJavaWithJavac/classes
				build/tmp/kotlin-classes/debugAndroidTest

			Associated compilations (1)
				debug

		compilation 'debugUnitTest' (target android (androidJvm)) (compiled by androidDebugUnitTestClasses task)

			Source sets (3)

				androidUnitTestDebug -----
					src/androidUnitTestDebug/kotlin                                                  NOT EXISTS
					src/testDebug/java                                                               NOT EXISTS
					src/testDebug/kotlin                                                             NOT EXISTS

				androidUnitTest -----
					src/androidUnitTest/kotlin                                                       NOT EXISTS
					src/test/java                                                                    NOT EXISTS
					src/test/kotlin                                                                  NOT EXISTS

				commonTest -----
					src/commonTest/kotlin                                                            NOT EXISTS

			Output
				build/intermediates/javac/debugUnitTest/compileDebugUnitTestJavaWithJavac/classes
				build/tmp/kotlin-classes/debugUnitTest

			Associated compilations (1)
				debug

		compilation 'release' (target android (androidJvm)) (compiled by androidReleaseClasses task)

			Source sets (3)

				androidRelease -----
					src/androidRelease/kotlin                                                        NOT EXISTS
					src/release/java                                                                 NOT EXISTS
					src/release/kotlin                                                               NOT EXISTS

				androidMain -----
					src/androidMain/kotlin
					src/main/java                                                                    NOT EXISTS
					src/main/kotlin                                                                  NOT EXISTS

				commonMain -----
					src/commonMain/kotlin                                                            NOT EXISTS

			Output
				build/intermediates/javac/release/compileReleaseJavaWithJavac/classes
				build/tmp/kotlin-classes/release

		compilation 'releaseUnitTest' (target android (androidJvm)) (compiled by androidReleaseUnitTestClasses task)

			Source sets (3)

				androidUnitTestRelease -----
					src/androidUnitTestRelease/kotlin                                                NOT EXISTS
					src/testRelease/java                                                             NOT EXISTS
					src/testRelease/kotlin                                                           NOT EXISTS

				androidUnitTest -----
					src/androidUnitTest/kotlin                                                       NOT EXISTS
					src/test/java                                                                    NOT EXISTS
					src/test/kotlin                                                                  NOT EXISTS

				commonTest -----
					src/commonTest/kotlin                                                            NOT EXISTS

			Output
				build/intermediates/javac/releaseUnitTest/compileReleaseUnitTestJavaWithJavac/classes
				build/tmp/kotlin-classes/releaseUnitTest

			Associated compilations (1)
				release

	metadata ----- (common platform, 1 compilations)

		compilation 'main' (target metadata (common)) (compiled by metadataMainClasses task)

			Source sets (1)

				commonMain -----
					src/commonMain/kotlin                                                            NOT EXISTS

			Output
				build/classes/kotlin/metadata/main

== [Android Variants] ========================================================== (5)

	debug ===== (compiled by compileDebugJavaWithJavac task)

		Source sets (2)

			main -----
				src/androidMain/kotlin
				src/main/java                                                                    NOT EXISTS
				src/main/kotlin                                                                  NOT EXISTS

			debug -----
				src/androidDebug/kotlin                                                          NOT EXISTS
				src/debug/java                                                                   NOT EXISTS
				src/debug/kotlin                                                                 NOT EXISTS

	release ===== (compiled by compileReleaseJavaWithJavac task)

		Source sets (2)

			main -----
				src/androidMain/kotlin
				src/main/java                                                                    NOT EXISTS
				src/main/kotlin                                                                  NOT EXISTS

			release -----
				src/androidRelease/kotlin                                                        NOT EXISTS
				src/release/java                                                                 NOT EXISTS
				src/release/kotlin                                                               NOT EXISTS

	debugAndroidTest ===== (compiled by compileDebugAndroidTestJavaWithJavac task)

		Source sets (2)

			androidTest -----
				src/androidInstrumentedTest/kotlin                                               NOT EXISTS
				src/androidTest/java                                                             NOT EXISTS
				src/androidTest/kotlin                                                           NOT EXISTS

			androidTestDebug -----
				src/androidInstrumentedTestDebug/kotlin                                          NOT EXISTS
				src/androidTestDebug/java                                                        NOT EXISTS
				src/androidTestDebug/kotlin                                                      NOT EXISTS

	debugUnitTest ===== (compiled by compileDebugUnitTestJavaWithJavac task)

		Source sets (2)

			test -----
				src/androidUnitTest/kotlin                                                       NOT EXISTS
				src/test/java                                                                    NOT EXISTS
				src/test/kotlin                                                                  NOT EXISTS

			testDebug -----
				src/androidUnitTestDebug/kotlin                                                  NOT EXISTS
				src/testDebug/java                                                               NOT EXISTS
				src/testDebug/kotlin                                                             NOT EXISTS

	releaseUnitTest ===== (compiled by compileReleaseUnitTestJavaWithJavac task)

		Source sets (2)

			test -----
				src/androidUnitTest/kotlin                                                       NOT EXISTS
				src/test/java                                                                    NOT EXISTS
				src/test/kotlin                                                                  NOT EXISTS

			testRelease -----
				src/androidUnitTestRelease/kotlin                                                NOT EXISTS
				src/testRelease/java                                                             NOT EXISTS
				src/testRelease/kotlin                                                           NOT EXISTS
```

 
Pay attention:
```
	Android sources NOT COVERED by kotlin source sets
		src/androidTestRelease/java                                            (androidTestRelease)
		src/androidTestRelease/kotlin                                          (androidTestRelease)
		src/testFixtures/java                                                  (testFixtures)
		src/testFixtures/kotlin                                                (testFixtures)
		src/testFixturesDebug/java                                             (testFixturesDebug)
		src/testFixturesDebug/kotlin                                           (testFixturesDebug)
		src/testFixturesRelease/java                                           (testFixturesRelease)
		src/testFixturesRelease/kotlin                                         (testFixturesRelease)


	Kotlin sources NOT COVERED by android source sets
		src/commonMain/kotlin                                                  (commonMain)
		src/commonTest/kotlin                                                  (commonTest)

```

That's why android plugin support is used for animalsniffer tasks creation - only kotlin common sources added to created tasks.


[Print animalsniffer tasks](../debug/tasks.md) task:
```
> Task :printAnimalsnifferTasks


	animalsnifferDebug                  [default]       for 'debug' android variant
		report: build/reports/animalsniffer/debug.text
		depends on: debugAnimalsnifferClassesCollector
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/tmp/kotlin-classes/debug
		sources:
			src/androidDebug/kotlin                                                          NOT EXISTS
			src/androidMain/kotlin
			src/commonMain/kotlin                                                            NOT EXISTS
			src/debug/java                                                                   NOT EXISTS
			src/debug/kotlin                                                                 NOT EXISTS
			src/main/java                                                                    NOT EXISTS
			src/main/kotlin                                                                  NOT EXISTS


	animalsnifferDebugAndroidTest                       for 'debugAndroidTest' android test component
		report: build/reports/animalsniffer/debugAndroidTest.text
		depends on: debugAndroidTestAnimalsnifferClassesCollector
		signatures: 
			java16-sun-1.0.signature
		classes:
			<empty>
		sources:
			src/androidInstrumentedTest/kotlin                                               NOT EXISTS
			src/androidInstrumentedTestDebug/kotlin                                          NOT EXISTS
			src/androidTest/java                                                             NOT EXISTS
			src/androidTest/kotlin                                                           NOT EXISTS
			src/androidTestDebug/java                                                        NOT EXISTS
			src/androidTestDebug/kotlin                                                      NOT EXISTS


	animalsnifferDebugUnitTest                          for 'debugUnitTest' android test component
		report: build/reports/animalsniffer/debugUnitTest.text
		depends on: debugUnitTestAnimalsnifferClassesCollector
		signatures: 
			java16-sun-1.0.signature
		classes:
			<empty>
		sources:
			src/androidUnitTest/kotlin                                                       NOT EXISTS
			src/androidUnitTestDebug/kotlin                                                  NOT EXISTS
			src/commonTest/kotlin                                                            NOT EXISTS
			src/test/java                                                                    NOT EXISTS
			src/test/kotlin                                                                  NOT EXISTS
			src/testDebug/java                                                               NOT EXISTS
			src/testDebug/kotlin                                                             NOT EXISTS


	animalsnifferRelease                [default]       for 'release' android variant
		report: build/reports/animalsniffer/release.text
		depends on: releaseAnimalsnifferClassesCollector
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/kotlinToolingMetadata
			build/tmp/kotlin-classes/release
		sources:
			src/androidMain/kotlin
			src/androidRelease/kotlin                                                        NOT EXISTS
			src/commonMain/kotlin                                                            NOT EXISTS
			src/main/java                                                                    NOT EXISTS
			src/main/kotlin                                                                  NOT EXISTS
			src/release/java                                                                 NOT EXISTS
			src/release/kotlin                                                               NOT EXISTS


	animalsnifferReleaseUnitTest                        for 'releaseUnitTest' android test component
		report: build/reports/animalsniffer/releaseUnitTest.text
		depends on: releaseUnitTestAnimalsnifferClassesCollector
		signatures: 
			java16-sun-1.0.signature
		classes:
			<empty>
		sources:
			src/androidUnitTest/kotlin                                                       NOT EXISTS
			src/androidUnitTestRelease/kotlin                                                NOT EXISTS
			src/commonTest/kotlin                                                            NOT EXISTS
			src/test/java                                                                    NOT EXISTS
			src/test/kotlin                                                                  NOT EXISTS
			src/testRelease/java                                                             NOT EXISTS
			src/testRelease/kotlin                                                           NOT EXISTS
```

## Changing default tasks

In the example 2 there are two default tasks: `animalsnifferJvmMain` and `animalsnifferJsMain`.
If we want to preserve only jvmMain:

```groovy
animalsniffer {
    defaultTargets = ['jvmMain']
}
```

or with method

```groovy
animalsniffer {
    defaultTargets 'jvmMain'
}
```

!!! tip
    If you want to avoid all animalsniffer tasks:
    ```groovy
    defaultTargets = []
    ```

### Test tasks

Test sources are not checked by default. If you want to check test sources:

```groovy
animalsniffer {
    checkTestSources = true
}
```

After that `defaultTargets` option could be used to limit test tasks. 
