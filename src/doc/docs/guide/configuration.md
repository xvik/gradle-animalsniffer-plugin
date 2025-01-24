## Configuration

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

| Property              | Description                                                                                                                                                        | Default value                                   |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| toolVersion           | AnimalSniffer version                                                                                                                                              | {{ gradle.animalnsifferVersion }}               |
| sourceSets            | Source sets to check (DEPRECATED: use defaultTargets instead)                                                                                                      | all source sets, except tests                   |
| defaultTargets        | Target names (source set, android variant, kotlin platform) to check by default                                                                                    | all targets, except tests                       |
| checkTestSources      | Check test sources                                                                                                                                                 | false                                           |
| failWithoutSignatures | Fail check task if no signatures declared                                                                                                                          | true                                            |
| ignoreFailures        | False to stop build when violations found, true to continue                                                                                                        | false                                           |
| debug                 | Log animalsniffer configuration (useful in case of configuration problems)                                                                                         | false                                           |
| reportsDir            | Reports directory                                                                                                                                                  | file("$project.buildDir/reports/animalsniffer") |
| annotation            | Annotation class to avoid check under annotated block                                                                                                              |                                                 |
| ignore                | Ignore usage of classes, not mentioned in signature                                                                                                                |                                                 |
| signatures            | Signatures to use for check                                                                                                                                        | `configurations.signature`                      |
| excludeJars           | Patterns to exclude jar names from classpath. Required for [library signatures](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures) usage |                                                 |
| cache                 | [Cache configuration](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Check-task-performance)                                                             | By default, cache disabled                      |

**NOTE**: `ignore` does not exclude your classes from check, it allows you to use classes not mentioned in the
signature.
See more details above.

### Tasks

The animalsniffer task is registered for each source set:

* `animalsnifferMain` - run AnimalAniffer for compiled main classes
* `animalsnifferTest` - run AnimalSniffer for compiled test classes
* `animalsniffer[SourceSet]` - run AnimalSniffer for compiled `[SourceSet]` classes

The `check` task will depend only on tasks from configured in `animalsniffer.sourceSets` source sets.

Tasks support text report, enabled by default.

To disable reports for a task:

```groovy
animalsnifferMain.reports.text.enabled = false
```

or for all tasks:

```groovy
tasks.withType(AnimalSniffer) {
    reports.text.enabled = false
}
```

Animalsniffer task is a [SourceTask](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.SourceTask.html) and may
be configured
to include/exclude classes from check.

NOTE: The task operates on compiled classes and not sources! Be careful when defining patterns.

For example, to exclude classes in a 'invalid' subpackage from check:

```groovy
animalsnifferMain {
    exclude('**/invalid/*')
}
```
