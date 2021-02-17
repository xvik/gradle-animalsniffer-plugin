# gradle-animalsniffer-plugin
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](http://www.opensource.org/licenses/MIT)
[![Build Status](https://img.shields.io/travis/xvik/gradle-animalsniffer-plugin.svg)](https://travis-ci.org/xvik/gradle-animalsniffer-plugin)
[![Appveyor build status](https://ci.appveyor.com/api/projects/status/github/xvik/gradle-animalsniffer-plugin?svg=true)](https://ci.appveyor.com/project/xvik/gradle-animalsniffer-plugin)
[![codecov](https://codecov.io/gh/xvik/gradle-animalsniffer-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/xvik/gradle-animalsniffer-plugin)

### About

Gradle [AnimalSniffer](http://www.mojohaus.org/animal-sniffer/) plugin for Java or Groovy projects.
AnimalSniffer is used to check compatibility with lower Java versions (when compiling with a newer Java version) or Android (SDK version).

It is implemented the same way as core Gradle quality plugins (Checkstyle, PMD etc):
A task is registered for each source set (animalsnifferMain, animalsnifferTest) and attached to the `check` task.

Advanced features:
* [Signature build task](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Buid-project-signature)
* [Check task classpath caching](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Check-task-performance) to speed-up subsequent checks (useful when check runs often without clean)
* [Merging check signatures](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures) (when small 3rd party lib signatures are used)
* [Viewing signature content task](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/View-signature-content)

#### Applicability

If you're using JDK 9 or above then you can use the [--release](https://docs.oracle.com/en/java/javase/11/tools/javac.html#GUID-AEEC9F07-CB49-4E96-8BC7-BCC2C7F725C9)
flag instead of the plugin:

```groovy
compileJava {
  options.compilerArgs.addAll(['--release', '7'])
}
```    

Direct `release` option support [available in gradle 6.6](https://docs.gradle.org/6.6/release-notes.html#javacompile-release)
([feature discussion](https://github.com/gradle/gradle/issues/2510)).

The plugin could still be useful:

* For Android projects to check API compatibility (because Android API signatures are published).
* To check strong compatibility with some library: you'll need to generate signatures for this library and
will be able to use them to check project compatibility (on API level, ofc) with older library versions.
  
##### Summary

* Configuration extensions: 
    - `animalsniffer` - check configuration
    - `animalsnifferSignature` - signature [build configuration](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Buid-project-signature) (optional)
* Tasks:
    - `check[Main]` - check source set task 
    - `animalsnifferSignature` - build signature (active when `animalsnifferSignature` configuration declared)
    - `type:BuildSignatureTask` - custom signature build task may be used to [merge signatures](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures)
    - `type:SignatureInfoTask` - view signature ["contents"](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/View-signature-content)  
* Dependencies configuration: `signature` - signatures for check    

### Setup

[![JCenter](https://img.shields.io/bintray/v/vyarus/xvik/gradle-animalsniffer-plugin.svg?label=jcenter)](https://bintray.com/vyarus/xvik/gradle-animalsniffer-plugin/_latestVersion)
[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/gradle-animalsniffer-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/gradle-animalsniffer-plugin)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/ru/vyarus/animalsniffer/ru.vyarus.animalsniffer.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=plugins%20portal)](https://plugins.gradle.org/plugin/ru.vyarus.animalsniffer)


```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'ru.vyarus:gradle-animalsniffer-plugin:1.5.3'
    }
}
apply plugin: 'ru.vyarus.animalsniffer'
```

OR 

```groovy
plugins {
    id 'ru.vyarus.animalsniffer' version '1.5.3'
}
```

#### Compatibility

**IMPORTANT**: The plugin only works when the `java` (or `java-library`) or `groovy` plugin is enabled, otherwise nothing will be registered.
There is no support for the Android plugin (the `java` plugin must be used to perform the animalsniffer check).

The plugin is compiled for Java 8, and is compatible with Java 11.

Gradle | Version
--------|-------
5-6     | 1.5.3
4.x     | [1.4.6](https://github.com/xvik/gradle-animalsniffer-plugin/tree/1.4.6)

### Usage

Additional tasks will be assigned to the `check` task. So animalsniffer checks will be executed during:

```bash
$ gradlew check
```

#### Signatures

AnimalSniffer requires a signature file to check against. To define a signature (or multiple signatures) use
the `signature` configuration.

To check Java version compatibility:

```groovy
repositories { mavenCentral() }
dependencies {
    signature 'org.codehaus.mojo.signature:java16:1.1@signature'
}
```

[Java signatures](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.codehaus.mojo.signature%22)

To check Android compatibility:

```groovy
repositories { mavenCentral() }
dependencies {
    signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
}
```

[Android signatures](http://search.maven.org/#search%7Cga%7C1%7Cg%3Anet.sf.androidscents.signature)

To check **both** Java version and Android compatibility:

```groovy
dependencies {
    signature 'org.codehaus.mojo.signature:java16:1.1@signature'
    signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
}
```

In the last case animalsniffer will run 2 times for each signature. You may see the same errors two times if a
class/method is absent in both signatures. Each error message in the log (and file) will also contain the
signature name to avoid confusion.

When no signatures are defined animalsniffer tasks will always pass.

You can also use custom libraries signatures to [check version compatibility](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures).

#### Scope

All project dependencies are excluded from the analysis: only classes from your source set are checked.

By default, all source sets are checked. To only check main sources:

```groovy
animalsniffer {
    sourceSets = [sourceSets.main]
}
```  

#### Output

Violations are always printed to the console. Example output:

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

NOTE: when multiple signatures are used, output will contain the signature name in the error message to avoid confusion.

#### Suppress violations

An annotation could be used to suppress violations:
[examples](http://www.mojohaus.org/animal-sniffer/animal-sniffer-annotations/index.html)

##### Default annotation

Add dependency on the annotation artifact:

```groovy
implementation "org.codehaus.mojo:animal-sniffer-annotations:1.16"
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

Your project could target multiple Java versions and so reference classes, not present in a signature.

For example, your implementation could try to use Java 7 `Paths` and if the class is not available, fall back
to the Java 6 implementation. In this case `Paths` could be added to the ignored classes:

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
[the documentation](http://www.mojohaus.org/animal-sniffer/animal-sniffer-ant-tasks/examples/checking-signatures.html#Ignoring_classes_not_in_the_signature).

### Configuration

Configuration example:

```groovy
animalsniffer {
    toolVersion = '1.20'
    sourceSets = [sourceSets.main]
    ignoreFailures = true
    reportsDir = file("$project.buildDir/animalsnifferReports")
    annotation = 'com.mypackage.MyAnnotation'
    ignore = ['java.nio.file.Paths']
}
```

There are no required configurations - the plugin will generate defaults for all of them.

| Property | Description |  Default value |
|----------|-------------|----------------|
| toolVersion | AnimalSniffer version | 1.20 |
| sourceSets | Source sets to check | all source sets |
| ignoreFailures | False to stop build when violations found, true to continue | false |
| reportsDir | Reports directory | file("$project.buildDir/reports/animalsniffer") |
| annotation | Annotation class to avoid check under annotated block | |
| ignore    | Ignore usage of classes, not mentioned in signature | |
| signatures    | Signatures to use for check | `configurations.signature`|
| excludeJars    | Patterns to exclude jar names from classpath. Required for [library signatures](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures) usage | |
| cache    | [Cache configuration](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Check-task-performance) | By default, cache disabled|

**NOTE**: `ignore` does not exclude your classes from check, it allows you to use classes not mentioned in the signature.
See more details above.

### Tasks

The animalsniffer task is registered for each source set:
* `animalsnifferMain` - run AnimalAniffer for compiled main classes
* `animalsnifferTest` - run AnimalSniffer for compiled test classes
* `animalsnifferSourceSet` - run AnimalSniffer for compiled SourceSet classes

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

Animalsniffer task is a [SourceTask](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.SourceTask.html) and may be configured 
to include/exclude classes from check.

NOTE: The task operates on compiled classes and not sources! Be careful when defining patterns.

For example, to exclude classes in a 'invalid' subpackage from check:

```groovy
animalsnifferMain {
    exclude('**/invalid/*')
}
```

### Advanced features

Read wiki for advanced features:

* [Build your project signature](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Buid-project-signature)
* [Optimize often check task calls](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Check-task-performance) to speed-up subsequent checks (useful when check runs often without clean)
* [Merging library signatures](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/Library-signatures) (when small 3rd party lib signatures used)
* [View signature content](https://github.com/xvik/gradle-animalsniffer-plugin/wiki/View-signature-content)

### Might also like

* [quality-plugin](https://github.com/xvik/gradle-quality-plugin) - java and groovy source quality checks
* [mkdocs-plugin](https://github.com/xvik/gradle-mkdocs-plugin) - project documentation generator
* [pom-plugin](https://github.com/xvik/gradle-pom-plugin) - improves pom generation
* [java-lib-plugin](https://github.com/xvik/gradle-java-lib-plugin) - avoid boilerplate for java or groovy library project
* [github-info-plugin](https://github.com/xvik/gradle-github-info-plugin) - pre-configure common plugins with github related info
* [java-library generator](https://github.com/xvik/generator-lib-java) - java library project generator

---
[![gradle plugin generator](http://img.shields.io/badge/Powered%20by-%20Gradle%20plugin%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-gradle-plugin) 
