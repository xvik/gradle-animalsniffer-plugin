# gradle-animalsniffer-plugin
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](http://www.opensource.org/licenses/MIT)
[![Build Status](https://img.shields.io/travis/xvik/gradle-animalsniffer-plugin.svg)](https://travis-ci.org/xvik/gradle-animalsniffer-plugin)

### About

Gradle [AnimalSniffer](http://www.mojohaus.org/animal-sniffer/) plugin for Java or groovy projects.
AnimalSniffer used to check compatibility with lower java version (when compiling with newer java) or android (sdk version).

Implemented the same way as core gradle qulity plugins (checkstyle, pmd etc):
* Task registered for each source set (animalsnifferMain, animalsnifferTest) and attached to `check` task
* Main configuration through `animalsniffer` closure
* Configurable tool version
* Text report

Advanced features:
* [Signature build task](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Buid-project-signature)
* [Check task classpath caching](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Check-task-performance) to speed-up consequent checks (useful when check runs often without clean)
* [Merging check signatures](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures) (when small 3rd party lib signatures used)
* [Viewing signature content task](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/View-signature-content)

### Setup

Releases are published to [bintray jcenter](https://bintray.com/vyarus/xvik/gradle-animalsniffer-plugin/), 
[maven central](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/gradle-animalsniffer-plugin) and 
[gradle plugins portal](https://plugins.gradle.org/plugin/ru.vyarus.animalsniffer).

[![JCenter](https://img.shields.io/bintray/v/vyarus/xvik/gradle-animalsniffer-plugin.svg?label=jcenter)](https://bintray.com/vyarus/xvik/gradle-animalsniffer-plugin/_latestVersion)
[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/gradle-animalsniffer-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/gradle-animalsniffer-plugin)

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'ru.vyarus:gradle-animalsniffer-plugin:1.4.1'
    }
}
apply plugin: 'ru.vyarus.animalsniffer'
```

OR 

```groovy
plugins {
    id 'ru.vyarus.animalsniffer' version '1.4.1'
}
```

**IMPORTANT**: plugin must be declared after `java` or `groovy` plugin, otherwise nothing will be registered. 

Starting from version 1.1.0 gradle >= 2.14 is required. For older gradle use version 1.0.1.

### Usage

Additional tasks will be assigned to `check` task. So animalsniffer checks will be executed during:

```bash
$ gradlew check
```

#### Signatures

AnimalSniffer requires signature file to check against. To define signature (or multiple signatures) use
`signature` configuration.

Check java version compatibility:

```groovy
repositories { mavenCentral() }
dependencies {
    signature 'org.codehaus.mojo.signature:java16:1.1@signature'
}
```

[Java signatures](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.codehaus.mojo.signature%22)

Check android compatibility:

```groovy
repositories { mavenCentral() }
dependencies {
    signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
}
```

[Android signatures](http://search.maven.org/#search%7Cga%7C1%7Cg%3Anet.sf.androidscents.signature)

Check **both** java version and android compatibility:

```groovy
dependencies {
    signature 'org.codehaus.mojo.signature:java16:1.1@signature'
    signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
}
```

In the last case animalsniffer will run 2 times for each signature. You may see the same errors two times if
class/method is absent in both signatures. Each error message in log (and file) will also contain signature name to
avoid confusion.

When no signatures defined animalsniffer tasks will always pass.

You can also use custom libraries signatures to [check version compatibility](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures).

#### Scope

All project dependencies are excluded from analysis: only classes from your source set are checked.

By default, all source sets are checked. To check only main sources:

```groovy
animalsniffer {
    sourceSets = [sourceSets.main]
}
```  

#### Output

Violations are always printed to console. Example output:

```
2 AnimalSniffer violations were found in 1 files. See the report at: file:///myproject/build/reports/animalsniffer/main.text

[Undefined reference] invalid.(Sample.java:9)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.java:14)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])
```

NOTE: text report file will contain simplified report (error per line):

```
invalid.Sample:9  Undefined reference: int Boolean.compare(boolean, boolean)
invalid.Sample:14  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])
```

NOTE: when multiple signatures used, output will contain signature name in the error message to void confusion.

#### Suppress violations

Annotation could be used to suppress violations: 
[examples](http://www.mojohaus.org/animal-sniffer/animal-sniffer-annotations/index.html)

##### Default annotation

Add dependency on annotation:

```groovy
compile "org.codehaus.mojo:animal-sniffer-annotations:1.15"
``` 

Use `provided` scope if you can. 
Annotation is configured by default, so you can simply use annotation to suppress violation:

```groovy
@IgnoreJRERequirement
private Optional param;
```

##### Custom annotation

You can define your own annotation:

```groovy
package com.mycompany

@Retention(RetentionPolicy.CLASS)
@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface SuppressSignatureCheck {}
```

Configure annotation:

```groovy
animalsniffer {
    annotation = 'com.mycompany.SuppressSignatureCheck'
}
```

Now check will skip blocks annotated with your annotation: 

```groovy
@SuppressSignatureCheck
private Optional param;
```

#### Extend signature

Project could target multiple java versions and so reference classes, not present in signature.

For example, implementation could try to use java 7 `Paths` and if class is not available, fall back
to java 6 implementation. In this case `Paths` could be added to ignored classes:

```groovy
animalsniffer {
    ignore 'java.nio.file.Paths'
}
``` 

Now usages of `Paths` will not cause warnings.

Multiple ignored classes could be defined:

```groovy
animalsniffer {
    ignore 'java.nio.file.Paths', 'some.other.Class'
}
```

Or

```groovy
animalsniffer {
    ignore 'java.nio.file.Paths'
    ignore 'some.other.Class'
}
```

Or by directly assigning collection:

```groovy
animalsniffer {
    ignore  = ['java.nio.file.Paths', 'some.other.Class']
}
```

Entire packages could be ignored using asterisk:

```groovy
animalsniffer {
    ignore 'some.pkg.*'
}
```

See more info in 
[documentation](http://www.mojohaus.org/animal-sniffer/animal-sniffer-ant-tasks/examples/checking-signatures.html#Ignoring_classes_not_in_the_signature).

### Configuration

Configuration example:

```groovy
animalsniffer {
    toolVersion = '1.15'
    sourceSets = [sourceSets.main]
    ignoreFailures = true
    reportsDir = file("$project.buildDir/animalsnifferReports")
    annotation = 'com.mypackage.MyAnnotation'
    ignore = ['java.nio.file.Paths']
}
```

There are no required configurations - plugin will generate defaults for all of them.

| Property | Description |  Default value |
|----------|-------------|----------------|
| toolVersion | AnimalSniffer version | 1.15 |
| sourceSets | Source sets to check | all source sets |
| ignoreFailures | False to stop build when violations found, true to continue | false |
| reportsDir | Reports directory | file("$project.buildDir/reports/animalsniffer") |
| annotation | Annotation class to avoid check under annotated block | |
| ignore    | Ignore usage of classes, not mentioned in signature | |
| signatures    | Signatures to use for check | `configurations.signature`|
| excludeJars    | Patterns to exclude jar names from classpath. Required for [library signatures](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures) usage | |
| cache    | [Cache configuration](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Check-task-performance) | By default, cache disabled|

**NOTE**: `ignore` does not exclude your classes from check, it allows you using classes not mentioned in signature.
See more details above.

### Tasks

Animalsniffer task is registered for each source set:
* `animalsnifferMain` - run AnimalAniffer for compiled main classes
* `animalsnifferTest` - run AnimalSniffer for compiled test classes
* `animalsnifferSourceSet` - run AnimalSniffer for compiled SourceSet classes

`check` task will depend only on tasks from configured in `animalsniffer.sourceSets` source sets.

Tasks support text report, enabled by default.

To disable reports for task:

```groovy
animalsnifferMain.reports.text.enabled = false
```

or for all tasks:

```groovy
tasks.withType(AnimalSniffer) {
    reports.text.enabled = false
}
```

Animalsniffer task is a [SourceTask](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.SourceTask.html) and may be configured 
to include/exclude classes from check.

NOTE: task operate on compiled classes and not sources! Be careful when defining patterns.

For example, to exclude classes in 'invalid' subpackage from check:

```groovy
animalsnifferMain {
    exclude('**/invalid/*')
}
```

### Advanced features

Read wiki for advanced features:

* [Build your project signature](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Buid-project-signature)
* [Optimize often check task calls](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Check-task-performance) to speed-up consequent checks (useful when check runs often without clean)
* [Merging library signatures](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures) (when small 3rd party lib signatures used)
* [View signature content](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/View-signature-content)

### Might also like

* [quality-plugin](https://github.com/xvik/gradle-quality-plugin) - java and groovy source quality checks
* [pom-plugin](https://github.com/xvik/gradle-pom-plugin) - improves pom generation
* [java-lib-plugin](https://github.com/xvik/gradle-java-lib-plugin) - avoid boilerplate for java or groovy library project
* [github-info-plugin](https://github.com/xvik/gradle-github-info-plugin) - pre-configure common plugins with github related info
* [java-library generator](https://github.com/xvik/generator-lib-java) - java library project generator

---
[![gradle plugin generator](http://img.shields.io/badge/Powered%20by-%20Gradle%20plugin%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-gradle-plugin) 
