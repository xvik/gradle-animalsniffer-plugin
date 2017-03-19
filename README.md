# gradle-animalsniffer-plugin
[![License](http://img.shields.io/badge/license-MIT-blue.svg)](http://www.opensource.org/licenses/MIT)
[![Build Status](http://img.shields.io/travis/xvik/gradle-animalsniffer-plugin.svg)](https://travis-ci.org/xvik/gradle-animalsniffer-plugin)

### About

Gradle [AnimalSniffer](http://www.mojohaus.org/animal-sniffer/) plugin for Java projects.
AnimalSniffer used to check compatibility with lower java version (when compiling with newer java) or to check library compatibility with android.

Implemented the same way as core gradle qulity plugins (checkstyle, pmd etc):
* Task registered for each source set (animalsnifferMain, animalsnifferTest) and attached to `check` task
* Main configuration through `animalsniffer` closure
* Configurable tool version
* Text report

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
        classpath 'ru.vyarus:gradle-animalsniffer-plugin:1.2.0'
    }
}
apply plugin: 'ru.vyarus.animalsniffer'
```

OR 

```groovy
plugins {
    id 'ru.vyarus.animalsniffer' version '1.2.0'
}
```

Starting from version 1.1.0 gradle >= 2.14 is required. For older gradle use version 1.0.1.

### Usage

Additional tasks will be assigned to `check` task. So animalsniffer checks will be executed during:

```bash
$ gradlew check
```

AnimalSniffer requires signature file to check against. To define signature (or multiple signatures) use
`signature` configuration:

```groovy
repositories { mavenCentral() }
dependencies {
    signature 'org.codehaus.mojo.signature:java16:1.1@signature'
}
```

* [Java signatures] (http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.codehaus.mojo.signature%22)
* [Android signatures](http://search.maven.org/#search%7Cga%7C1%7Cg%3Anet.sf.androidscents.signature)

When no signatures defined animalsniffer tasks will always pass.

All project dependencies are excluded from analysis: only classes from your source set are checked.

By default, all source sets are checked. To check only main sources:

```groovy
animalsniffer {
    sourceSets = [sourceSets.main]
}
```  

Violations are always printed to console. Example output:

```
2 AnimalSniffer violations were found in 1 files. See the report at: file:///myproject/build/reports/animalsniffer/main.text

invalid.Sample:9  Undefined reference: int Boolean.compare(boolean, boolean)
invalid.Sample:14  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])
```

#### Suppress violations

Annotation could be used to suppress violations.

[Moivation and examples](http://www.mojohaus.org/animal-sniffer/animal-sniffer-annotations/index.html)

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

### Configuration

Configuration example:

```groovy
animalsniffer {
    toolVersion = '1.15'
    sourceSets = [sourceSets.main]
    ignoreFailures = true
    reportsDir = file("$project.buildDir/animalsnifferReports")
    annotation = 'com.mypackage.MyAnnotation'
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


### Tasks

Animalsniffer task is registered for each source set:
* `animalsnifferMain` - run AnimalAniffer for compiled main classes
* `animalsnifferTest` - run AnimalSniffer for compiled test classes
* `animalsnifferSourceSet` - run AnimalSniffer for compiled SourceSet classes

`check` task will depend only on taks from configured in `animalsniffer.sourceSets` source sets.

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

### Might also like

* [quality-plugin](https://github.com/xvik/gradle-quality-plugin) - java and groovy source quality checks
* [pom-plugin](https://github.com/xvik/gradle-pom-plugin) - improves pom generation
* [java-lib-plugin](https://github.com/xvik/gradle-java-lib-plugin) - avoid boilerplate for java or groovy library project
* [github-info-plugin](https://github.com/xvik/gradle-github-info-plugin) - pre-configure common plugins with github related info
* [java-library generator](https://github.com/xvik/generator-lib-java) - java library project generator

-
[![gradle plugin generator](http://img.shields.io/badge/Powered%20by-%20Gradle%20plugin%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-gradle-plugin) 
