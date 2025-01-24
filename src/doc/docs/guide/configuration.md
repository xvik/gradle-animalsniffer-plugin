# Configuration

Configuration example:

```groovy
animalsniffer {
    toolVersion = '1.23'
    // sourceSets = [sourceSets.main]
    defaultTargets 'main'
    ignoreFailures = true
    reportsDir = file("$project.buildDir/animalsnifferReports")
    annotation = 'com.mypackage.MyAnnotation'
    ignore = ['java.nio.file.Paths']
}
```

There are no required configurations — the plugin will generate defaults for all of them.

| Property              | Description                                                                                                 | Default value                                   |
|-----------------------|-------------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| toolVersion           | AnimalSniffer version                                                                                       | {{ gradle.animalsnifferVersion }}               |
| sourceSets            | Source sets to check (DEPRECATED: use defaultTargets instead)                                               | all source sets                                 |
| defaultTargets        | Target names (source set, android variant, kotlin platform) to check by default                             | all targets                                     |
| checkTestSources      | Check test sources                                                                                          | false                                           |
| failWithoutSignatures | Fail check task if no signatures declared                                                                   | true                                            |
| ignoreFailures        | False to stop build when violations found, true to continue                                                 | false                                           |
| debug                 | Log animalsniffer configuration (useful in case of configuration problems)                                  | false                                           |
| reportsDir            | Reports directory                                                                                           | file("$project.buildDir/reports/animalsniffer") |
| annotation            | Annotation class to avoid check under annotated block                                                       |                                                 |
| ignore                | Ignore usage of classes, not mentioned in signature                                                         |                                                 |
| signatures            | Signatures to use for check                                                                                 | `configurations.signature`                      |
| excludeJars           | Patterns to exclude jar names from classpath. Required for [library signatures](signature/library.md) usage |                                                 |
| cache                 | [Cache configuration](performance.md)                                                                       | By default, cache disabled                      |

**NOTE**: `ignore` does not exclude your classes from check, it allows you to use classes not mentioned in the
signature.
See more details above.

### Tasks

The animalsniffer task is registered for each "source set". For example:

* `animalsnifferMain` - run AnimalAniffer for compiled main classes
* `animalsnifferTest` - run AnimalSniffer for compiled test classes
* `animalsniffer[SourceSet]` - run AnimalSniffer for compiled `[SourceSet]` classes

Read task registration specifics: [java](use/java.md), [android][use/android.md], [multiplatform](use/multiplatform.md)

Tasks support text and CSV [reports](report.md), enabled by default.

To disable text reports for a task:

```groovy
animalsnifferMain.reports.text.enabled = false
```

or for all tasks:

```groovy
tasks.withType(AnimalSniffer) {
    reports.text.enabled = false
}
```

(CSV report can't be disabled)

Animalsniffer task is a [SourceTask](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.SourceTask.html) and may
be configured
to include/exclude classes from check.

!!! note 
    The task operates on compiled classes and not sources! Be careful when defining patterns.

For example, to exclude classes in a 'invalid' subpackage from check:

```groovy
animalsnifferMain {
    exclude('**/invalid/*')
}
```

#### Selecting tasks by type

Check tasks contain special properties to simplify selection:

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