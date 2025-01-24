# Check android project

For android project (`com.android.library` or `com.android.application` plugins version 7.4.0 or above)
animalsniffer tasks created for **variants and test components**.

Example:

```groovy
plugins {
    id 'com.android.application' version '7.4.0' // or recent 8.5.2
    id 'ru.vyarus.animalsniffer' version "{{ gradle.version }}"
}

android { ... }

dependencies {
    signature 'net.sf.androidscents.signature:android-api-level-21:5.0.1_r2@signature'
}
```

Part of [print sources task](../debug/sources.md) output:

```
== [SourceSets] ===============================================================

	Android application Source Sets ------------------------------------------------------ (12)

		androidTest ----- (consumed by test component debugAndroidTest)
			src/androidTest/java                                                             NOT EXISTS
			src/androidTest/kotlin                                                           NOT EXISTS

		androidTestDebug ----- (consumed by test component debugAndroidTest)
			src/androidTestDebug/java                                                        NOT EXISTS
			src/androidTestDebug/kotlin                                                      NOT EXISTS

		androidTestRelease -----
			src/androidTestRelease/java                                                      NOT EXISTS
			src/androidTestRelease/kotlin                                                    NOT EXISTS

		debug ----- (consumed by variant debug)
			src/debug/java                                                                   NOT EXISTS
			src/debug/kotlin                                                                 NOT EXISTS

		main ----- (consumed by variant release)
			src/main/java
			src/main/kotlin                                                                  NOT EXISTS

		release ----- (consumed by variant release)
			src/release/java                                                                 NOT EXISTS
			src/release/kotlin                                                               NOT EXISTS

		test ----- (consumed by unit test component releaseUnitTest)
			src/test/java                                                                    NOT EXISTS
			src/test/kotlin                                                                  NOT EXISTS

		testDebug ----- (consumed by unit test component debugUnitTest)
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
			src/testRelease/java                                                             NOT EXISTS
			src/testRelease/kotlin                                                           NOT EXISTS
			
== [Android Variants] ========================================================== (5)

	debug ===== (compiled by compileDebugJavaWithJavac task)

		Source sets (2)

			main -----
				src/main/java
				src/main/kotlin                                                                  NOT EXISTS

			debug -----
				src/debug/java                                                                   NOT EXISTS
				src/debug/kotlin                                                                 NOT EXISTS

	release ===== (compiled by compileReleaseJavaWithJavac task)

		Source sets (2)

			main -----
				src/main/java
				src/main/kotlin                                                                  NOT EXISTS

			release -----
				src/release/java                                                                 NOT EXISTS
				src/release/kotlin                                                               NOT EXISTS

	debugAndroidTest ===== (compiled by compileDebugAndroidTestJavaWithJavac task)

		Source sets (2)

			androidTest -----
				src/androidTest/java                                                             NOT EXISTS
				src/androidTest/kotlin                                                           NOT EXISTS

			androidTestDebug -----
				src/androidTestDebug/java                                                        NOT EXISTS
				src/androidTestDebug/kotlin                                                      NOT EXISTS

	debugUnitTest ===== (compiled by compileDebugUnitTestJavaWithJavac task)

		Source sets (2)

			test -----
				src/test/java                                                                    NOT EXISTS
				src/test/kotlin                                                                  NOT EXISTS

			testDebug -----
				src/testDebug/java                                                               NOT EXISTS
				src/testDebug/kotlin                                                             NOT EXISTS

	releaseUnitTest ===== (compiled by compileReleaseUnitTestJavaWithJavac task)

		Source sets (2)

			test -----
				src/test/java                                                                    NOT EXISTS
				src/test/kotlin                                                                  NOT EXISTS

			testRelease -----
				src/testRelease/java                                                             NOT EXISTS
				src/testRelease/kotlin                                                           NOT EXISTS
```

!!! important 
    *Fixture* sources aren't covered by variants (will not be checked).

In the example above, we have 2 variants and 3 test components:
'debug', 'release', 'debugAndroidTest', 'debugUnitTest' and 'releaseUnitTest'.
So the following animalsniffer tasks would be created:
`animalsnifferDebug`, `animalsnifferRelease`, `animalsnifferDebugAndroidTest`, 
`animalsnifferDebugUnitTest` and `animalsnifferReleaseUnitTest`

This could also be seen with [print animalsniffer tasks](../debug/tasks.md) task:

```
> Task :printAnimalsnifferTasks


	animalsnifferDebug                  [default]       for 'debug' android variant
		report: build/reports/animalsniffer/debug.text
		depends on: debugAnimalsnifferClassesCollector
		signatures: 
			android-api-level-21-5.0.1_r2.signature
		classes:
			build/intermediates/javac/debug/classes
		sources:
			build/generated/aidl_source_output_dir/debug/out                                 NOT EXISTS
			build/generated/renderscript_source_output_dir/debug/out                         NOT EXISTS
			build/generated/source/buildConfig/debug
			src/debug/java                                                                   NOT EXISTS
			src/debug/kotlin                                                                 NOT EXISTS
			src/main/java
			src/main/kotlin                                                                  NOT EXISTS


	animalsnifferDebugAndroidTest                       for 'debugAndroidTest' android test component
		report: build/reports/animalsniffer/debugAndroidTest.text
		depends on: debugAndroidTestAnimalsnifferClassesCollector
		signatures: 
			android-api-level-21-5.0.1_r2.signature
		classes:
			build/intermediates/javac/debugAndroidTest/classes
		sources:
			build/generated/aidl_source_output_dir/debugAndroidTest/out                      NOT EXISTS
			build/generated/renderscript_source_output_dir/debugAndroidTest/out              NOT EXISTS
			build/generated/source/buildConfig/androidTest/debug
			src/androidTest/java                                                             NOT EXISTS
			src/androidTest/kotlin                                                           NOT EXISTS
			src/androidTestDebug/java                                                        NOT EXISTS
			src/androidTestDebug/kotlin                                                      NOT EXISTS


	animalsnifferDebugUnitTest                          for 'debugUnitTest' android test component
		report: build/reports/animalsniffer/debugUnitTest.text
		depends on: debugUnitTestAnimalsnifferClassesCollector
		signatures: 
			android-api-level-21-5.0.1_r2.signature
		classes:
			<empty>
		sources:
			src/test/java                                                                    NOT EXISTS
			src/test/kotlin                                                                  NOT EXISTS
			src/testDebug/java                                                               NOT EXISTS
			src/testDebug/kotlin                                                             NOT EXISTS


	animalsnifferRelease                [default]       for 'release' android variant
		report: build/reports/animalsniffer/release.text
		depends on: releaseAnimalsnifferClassesCollector
		signatures: 
			android-api-level-21-5.0.1_r2.signature
		classes:
			build/intermediates/javac/release/classes
		sources:
			build/generated/aidl_source_output_dir/release/out                               NOT EXISTS
			build/generated/renderscript_source_output_dir/release/out                       NOT EXISTS
			build/generated/source/buildConfig/release
			src/main/java
			src/main/kotlin                                                                  NOT EXISTS
			src/release/java                                                                 NOT EXISTS
			src/release/kotlin                                                               NOT EXISTS


	animalsnifferReleaseUnitTest                        for 'releaseUnitTest' android test component
		report: build/reports/animalsniffer/releaseUnitTest.text
		depends on: releaseUnitTestAnimalsnifferClassesCollector
		signatures: 
			android-api-level-21-5.0.1_r2.signature
		classes:
			<empty>
		sources:
			src/test/java                                                                    NOT EXISTS
			src/test/kotlin                                                                  NOT EXISTS
			src/testRelease/java                                                             NOT EXISTS
			src/testRelease/kotlin                                                           NOT EXISTS
```

!!! note
    `[default]` indicates tasks executed with build

!!! tip
    Use [`animalsniffer.debug = true`](../debug/debug.md) to see class files, checked by animalsniffer task

## Changing default tasks

In many android projects, `debug` and `release` variants contain the same sources, so
it makes sense to run only one check during the build:

```groovy
animalsniffer {
    defaultTargets = ['debug']
}
```

or with method

```groovy
animalsniffer {
    defaultTargets 'debug'
}
```

!!! tip
    If you want to avoid all animalsniffer tasks:
    ```groovy
    defaultTargets = []
    ```

### Test tasks

Test tasks are not checked by default. If you want to check test sources:

```groovy
animalsniffer {
    checkTestSources = true
}
```

After that `defaultTargets` option could be used to limit test tasks. 

## Support specific

New android api requires a task to get access to android sources location (android plugin configures task - no way 
to just get required information). That's why additional tasks registered only for collecting
android configuration: `debugAnimalsnifferClassesCollector`, `releaseAnimalsnifferClassesCollector`, `debugAndroidTestAnimalsnifferClassesCollector` etc.
These tasks do nothing - they just receive required configurations to be used in animalsniffer task.