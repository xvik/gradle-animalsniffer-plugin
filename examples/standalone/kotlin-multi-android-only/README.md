This is a Kotlin Multiplatform project targeting Android.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

Project generated by https://kmp.jetbrains.com/

------------------------------------------

```
> Task :composeApp:printAnimalsnifferTasks


        animalsnifferDebug                  [default]       for 'debug' android variant
                report: composeApp/build/reports/animalsniffer/debug.text
                depends on: debugAnimalsnifferClassesCollector
                signatures: 
                        java16-sun-1.0.signature
                classes:
                        composeApp/build/tmp/kotlin-classes/debug
                sources:
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidDebugResourceAccessors NOT EXISTS
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidMainResourceAccessors NOT EXISTS
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidMainResourceCollectors
                        composeApp/build/generated/compose/resourceGenerator/kotlin/commonMainResourceAccessors
                        composeApp/build/generated/compose/resourceGenerator/kotlin/commonMainResourceCollectors
                        composeApp/build/generated/compose/resourceGenerator/kotlin/commonResClass
                        composeApp/src/androidDebug/kotlin                                               NOT EXISTS
                        composeApp/src/androidMain/kotlin
                        composeApp/src/commonMain/kotlin                                                 NOT EXISTS
                        composeApp/src/debug/java                                                        NOT EXISTS
                        composeApp/src/debug/kotlin                                                      NOT EXISTS
                        composeApp/src/main/java                                                         NOT EXISTS
                        composeApp/src/main/kotlin                                                       NOT EXISTS


        animalsnifferDebugAndroidTest                       for 'debugAndroidTest' android test component
                report: composeApp/build/reports/animalsniffer/debugAndroidTest.text
                depends on: debugAndroidTestAnimalsnifferClassesCollector
                signatures: 
                        java16-sun-1.0.signature
                classes:
                        <empty>
                sources:
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidInstrumentedTestDebugResourceAccessors NOT EXISTS
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidInstrumentedTestResourceAccessors NOT EXISTS
                        composeApp/src/androidInstrumentedTest/kotlin                                    NOT EXISTS
                        composeApp/src/androidInstrumentedTestDebug/kotlin                               NOT EXISTS
                        composeApp/src/androidTest/java                                                  NOT EXISTS
                        composeApp/src/androidTest/kotlin                                                NOT EXISTS
                        composeApp/src/androidTestDebug/java                                             NOT EXISTS
                        composeApp/src/androidTestDebug/kotlin                                           NOT EXISTS


        animalsnifferDebugUnitTest                          for 'debugUnitTest' android test component
                report: composeApp/build/reports/animalsniffer/debugUnitTest.text
                depends on: debugUnitTestAnimalsnifferClassesCollector
                signatures: 
                        java16-sun-1.0.signature
                classes:
                        <empty>
                sources:
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidUnitTestDebugResourceAccessors NOT EXISTS
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidUnitTestResourceAccessors NOT EXISTS
                        composeApp/build/generated/compose/resourceGenerator/kotlin/commonTestResourceAccessors NOT EXISTS
                        composeApp/src/androidUnitTest/kotlin                                            NOT EXISTS
                        composeApp/src/androidUnitTestDebug/kotlin                                       NOT EXISTS
                        composeApp/src/commonTest/kotlin                                                 NOT EXISTS
                        composeApp/src/test/java                                                         NOT EXISTS
                        composeApp/src/test/kotlin                                                       NOT EXISTS
                        composeApp/src/testDebug/java                                                    NOT EXISTS
                        composeApp/src/testDebug/kotlin                                                  NOT EXISTS


        animalsnifferRelease                [default]       for 'release' android variant
                report: composeApp/build/reports/animalsniffer/release.text
                depends on: releaseAnimalsnifferClassesCollector
                signatures: 
                        java16-sun-1.0.signature
                classes:
                        composeApp/build/kotlinToolingMetadata
                        composeApp/build/tmp/kotlin-classes/release
                sources:
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidMainResourceAccessors NOT EXISTS
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidMainResourceCollectors
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidReleaseResourceAccessors NOT EXISTS
                        composeApp/build/generated/compose/resourceGenerator/kotlin/commonMainResourceAccessors
                        composeApp/build/generated/compose/resourceGenerator/kotlin/commonMainResourceCollectors
                        composeApp/build/generated/compose/resourceGenerator/kotlin/commonResClass
                        composeApp/src/androidMain/kotlin
                        composeApp/src/androidRelease/kotlin                                             NOT EXISTS
                        composeApp/src/commonMain/kotlin                                                 NOT EXISTS
                        composeApp/src/main/java                                                         NOT EXISTS
                        composeApp/src/main/kotlin                                                       NOT EXISTS
                        composeApp/src/release/java                                                      NOT EXISTS
                        composeApp/src/release/kotlin                                                    NOT EXISTS


        animalsnifferReleaseUnitTest                        for 'releaseUnitTest' android test component
                report: composeApp/build/reports/animalsniffer/releaseUnitTest.text
                depends on: releaseUnitTestAnimalsnifferClassesCollector
                signatures: 
                        java16-sun-1.0.signature
                classes:
                        <empty>
                sources:
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidUnitTestReleaseResourceAccessors NOT EXISTS
                        composeApp/build/generated/compose/resourceGenerator/kotlin/androidUnitTestResourceAccessors NOT EXISTS
                        composeApp/build/generated/compose/resourceGenerator/kotlin/commonTestResourceAccessors NOT EXISTS
                        composeApp/src/androidUnitTest/kotlin                                            NOT EXISTS
                        composeApp/src/androidUnitTestRelease/kotlin                                     NOT EXISTS
                        composeApp/src/commonTest/kotlin                                                 NOT EXISTS
                        composeApp/src/test/java                                                         NOT EXISTS
                        composeApp/src/test/kotlin                                                       NOT EXISTS
                        composeApp/src/testRelease/java                                                  NOT EXISTS
                        composeApp/src/testRelease/kotlin                                                NOT EXISTS

*use [printAnimalsnifferSourceInfo] task to see project sources configuration details

```