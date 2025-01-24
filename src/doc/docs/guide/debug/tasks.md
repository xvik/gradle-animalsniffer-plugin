# Animalsniffer tasks debug

`printAnimalsnifferTasks` shows configurations for all registered animalsniffer tasks
(in contrast to [debug option](debug.md), showing data only for a currently executed task).
It is useful for checking overall configuration correctness, especially in android projects.

Example android output (simple java project output would be too simple):

```
> Task :printAnimalsnifferTasks


	animalsnifferDebug                  [default]       for 'debug' android variant
		report: build/reports/animalsniffer/debug.text
		depends on: debugAnimalsnifferClassesCollector
		signatures: 
			android-api-level-21-5.0.1_r2.signature
			java18-1.0.signature
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
			java18-1.0.signature
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
			java18-1.0.signature
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
			java18-1.0.signature
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
			java18-1.0.signature
		classes:
			<empty>
		sources:
			src/test/java                                                                    NOT EXISTS
			src/test/kotlin                                                                  NOT EXISTS
			src/testRelease/java                                                             NOT EXISTS
			src/testRelease/kotlin                                                           NOT EXISTS
```

Pay attention to `[default]` marker - all task with this marker are assigned to `check` task and so
would be executed by default with project build.