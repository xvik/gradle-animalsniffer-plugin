# 2.0.0 Release notes

## (breaking) Require gradle 7 or above

Dropped support for gradle 5 and 6

## (breaking) Check fails without signatures

Before, animalsniffer check task was skipped when no signatures declared.
Now it would fail. This was done to catch quite a common configuration error: absence of "signature" qualifier

wrong:
```groovy
dependencies {
    signature 'org.codehaus.mojo.signature:java16:1.1'
}
```

correct:
```groovy
dependencies {
    signature 'org.codehaus.mojo.signature:java16:1.1@signature'
}
```

With old behavior, it was hard to detect incorrect configuration.

### Old no-signatures behavior

Old behavior could be reverted with the new option:

```groovy
animalsniffer {
    failWithoutSignatures = false
}
```

### Projects building signature

The change above would also affect projects using animalsniffer plugin only to build signatures
because check tasks are always registered (and so would fail on build).

In this case, either disable no-signatures fail:

```groovy
animalsniffer {
    failWithoutSignatures = false
}
```

or detach all animalsniffer check tasks from the `check` task:

```groovy
animalsniffer {
    defaultTargets = []
}
```
(read more about the new option below)

!!! note
    The primary plugin function is signature checking and so no-signatures fail must be 
    enabled by default to prevent incorrect signatures configuration (unfortunately, quite common).
    Projects only building signatures would have to disable this check (sorry for inconvenience)

## Changed default tasks selection

Before, the only way to define which animalsniffer tasks will run by default with the build (assigned to `check` task)
was `sourceSets` option. And a common configuration was disabling test source checks (to check only main sources with the build):

```groovy
animalsniffer {
    sourceSets = [sourceSets.main]
}
```

Now, **test sources are not included by default**. So the configuration above is not required anymore.

There is a new configuration property `checkTestSources` to be able to enable test sources back:

```groovy
aniamlsniffer {
    checkTestSources = true  // by default false! 
}
```

`sourceSets` configuration is **deprecated**. It will still work, but now plugin 
supports android and kotlin multiplatform and this option simply can't cover all cases.

Instead, there is a new option `defaultTargets`: you can specify required targets there for
animalsniffer tasks to run with build.

Target name is:

1. [Source set](../guide/use/java.md) name for java plugins (java, kotlin, scala, groovy)
2. [Android variant or test component](../guide/use/android.md) name for Android
3. [Platform compilation](../guide/use/multiplatform.md) name for kotlin multiplatform

By default, `defaultTargets = null` which means making all non-test (no 'test' in target name) 
animalsniffer tasks default.

Target platforms could be set with property:

```groovy
animalsniffer {
    defaultTargets = ['main']
}
```

or with method:

```groovy
animalsniffer {
    defaultTargets 'main'
}
```

!!! tip
    If you want to avoid all animalsniffer tasks during build set:
    ```groovy
    defaultTargets = []
    ```

!!! note
    You can see all registered animalsniffer tasks with [`printAnimalsnifferTasks`](#print-animalsniffer-tasks) task

## Android projects support

!!! note "Acknowledgment"
    Thanks to [@LikeTheSalad](https://github.com/xvik/gradle-animalsniffer-plugin/pull/99) for initial 
    android support implementation. Without it, android support would not happen so soon 
    (it was the main trigger for all other changes in version 2.0).

Android support activated with `com.android.library` or `com.android.application` plugins.

Android [variants and test components](../guide/use/android.md) used for animalsniffer tasks source.
By default: `debug` and `release` variants and `debugAndroidTest`, `debugUnitTest` 
and `releaseUnitTest` test components.

As test animalsniffer tasks not enabled by default, then only `animalsnifferDebug` and `animalsnifferRelease` 
tasks would be assigned to `check` (to run as part of the build).

But, in many projects, dubug and release variants would contain **the same sources**,
so it would make sense to preserve only one of them:

```groovy
animalsniffer {
    defaultTargets 'debug'
}
```

!!! note
    I can't apply this by default in case of android plugin because I don't know the specifics of your exact project.

There is a complete [android project example](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/standalone/android-simple), 
created with the android studio, with animalsniffer plugin activated (no special errors applied).

### Collector task

The new android api requires an additional task to be able to get access to configured sources.
That's why plugin registers additinoal "collector" tasks (like `debugAnimalsnifferClassesCollector`) - 
it is used ONLY to receive android configuration (no actual action in task itself)


## Kotlin multiplatform projects support

Kotlin multiplatform support activated for `org.jetbrains.kotlin.multiplatform` plugin.

!!! important
    Before, it was required to apply `.withJvm()` in order to activate animalsniffer.
    **It is not required** anymore - complete support was implemented. 
    If you use `.withJvm()` plugin will not duplicate animlasniffer tasks (because it enables java-base plugin) - only
    multiplatform support would work.

[Platform compilations](../guide/use/multiplatform.md) used for animalsniffer tasks source.

For example, in a simple case with one platform:

```groovy
kotlin {
    jvm()
}
```

Two animalsniffer tasks would be registered: `animalsnifferJvmMain` and `animalsnifferJvmTest`.

!!! note
    Tasks for "metadata" (common platform) is not registered because these sources 
    are always included in each platform (and animalsniffer would check them) 

### Android

When multiplatform used with android plugin, then "android platform" would be ignored - direct android support
would be used instead (but missed kotlin sources would be added to android check tasks).

For example, suppose we have a project with two platforms:

```groovy
kotlin {
    androidTarget { ... }
    jvm()
}
```

Then animalsniffer tasks for jvm platform would be registered by multiplatform support and 
android tasks by android support: `animalsnifferDebug`, `animalsnifferDebugAndroidTest`,
`animalsnifferDebugUnitTest`, `animalsnifferJvmMain`, `animalsnifferJvmTest`, `animalsnifferRelease`,
`animalsnifferReleaseUnitTest`.

Default tasks (assigned to check) would be: `animalsnifferDebug`, `animalsnifferJvmMain` and `animalsnifferRelease` 

If you want to check only jvm sources with the build:

```groovy
animlasniffer {
    defaultTargets 'jvmMain'
}
```

There is a complete multiplatform project examples (generated with kotlin init site) with
configured animalsniffer plugin (but without errors):

* [Multiplatform: android only](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/standalone/kotlin-multi-android-only) - 1 multiplatform target
* [Multiplatform: android, desktop, server](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/standalone/kotlin-multi-android-desktop-server) - several multiplatform targets
* [Multiplatform: desktop, server](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/standalone/kotlin-multi-desktop-server) - 2 targets, without android

## Improved debug

As before, debug option (`animalsniffer.debug = true`) prints debug info just before
animalsniffer task execution (configured signatures, classes to check and source paths).

With java plugins, it was obvious what tasks plugin would register (task per source set). 
But with android and kotlin multiplatform plugins, it might not be obvious.

### Print animalsniffer tasks

A new `printAnimalsnifferTasks` was added to see all registered check tasks.

Java plugin sample:

```
> Task :printAnimalsnifferTasks


	animalsnifferMain                   [default]       for 'main' source set
		report: build/reports/animalsniffer/main.text
		depends on: classes
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/classes/java/main
		sources:
			src/main/java
```

Android plugin sample:

```
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
```

Kotlin multiplatform sample:

```
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
```

!!! note
    For each task, source origin is described, .e.g. "for kotlin platform 'jvm' compilation 'main'".
    `[default]` marker means that task is attached to `check` task (use `animalsniffer.defaultTaragets` to change it)

Read more in [docs](../guide/debug/tasks.md)

### Print animalsniffer sources

Another special task was added during android and multiplatform support development, 
to better understand compilation tasks and sources structure.

`printAnimalsnifferSourceInfo` prints registered plugins, compile tasks, all source sets, android and kotlin components.

This task might be useful for bug reports in case when animalsniffer task misses some sources.

Read more in [docs](../guide/debug/sources.md). See example outputs for [java](../guide/use/java.md), [android](../guide/use/android.md)
and [multiplatform](../guide/use/multiplatform.md)

## CSV report

There was a text report, looking like this (each line - error description):

```
invalid.Sample:1 (#field)  Undefined reference: java.nio.file.Path
invalid.Sample:13  Undefined reference: int Boolean.compare(boolean, boolean)
```

Now there is also a [CSV report](../guide/report.md#csv-report):

```
java16-sun-1.0;invalid.Sample.java;;field;java.nio.file.Path;false
java16-sun-1.0;invalid.Sample.java;13;;int Boolean.compare(boolean, boolean);false
```

This CSV report is required because now animalsniffer run inside gradle worker (which may run even in different jvm)
and the only way to get access to found errors in check task itself is an intermediate file.

This might be an internal file, but I preserved it as a task report to let other tools (if any) use it
for error representation (and avoid additional parsing).

## New task properties

Added properties to help distinguish animalsniffer tasks:

* `targetType` - task source origin: Java, Multiplatform, Android
* `targetName` - task target name (e.g. "main" (source set), "debug" (variant), "jvmMain" platform compilation) 

It might be useful if you need to configure tasks based on a registration source (for whatever reason).
For example:

```groovy
tasks.withType(AnimalSniffer).configureEach { task ->
    // no package required for TargetType enum because it's name registered as shortcut
    if (task.targetType == TargetType.Android) {
        // do something
    }
}
```

## Other changes

* Gradle configuration cache compatibility (required for gradle 9) 
* All animalsniffer tasks now belong to the `animalsniffer` group (in IDE, you could find all of them under animalsniffer tasks subfolder)
* Animalsniffer now used directly (with gradle worker) and not through intermediate ant tasks
* Renamed caching tasks to differentiate them with check tasks: before, cache tasks prefix was "animalsnifferCache", now "cache". 
  Also renamed target (cache) signature path: `/animalsniffer/cache/[checkTargetName]/[checkTaskName]Cache.sig` 
* Change source field names representation in reports (in some cases, animalsniffer shows class field name instead of source line):
  before field was shown as "fieldName field" now "#fieldName"

## Migration notes

Plugin would work as before, except for projects **only building signatures** (read above).
For such projects, just disable no-signatures check:

```groovy
animalsniffer {
    failWithoutSignatires = false
}
```

If you were **configuring sourceSets** like this: `sourceSets = [sourceSets.main]` - it is not required anymore, just remove.
For more complex source sets configuration, consider migration into `defaultTargets` option.

In **kotlin multiplatform** project, if you use `.withJava()` only to enable animalsniffer checks - it is not required anymore
(and could be removed if not needed).

Test tasks are **not assigned to check** (test sources not checked with the build, by default). To revert old behaviour:

```groovy
animalsniffer {
    checkTestSources = true
}
```
