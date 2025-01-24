# Getting started

## Installation

[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/gradle-animalsniffer-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/gradle-animalsniffer-plugin)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/ru/vyarus/animalsniffer/ru.vyarus.animalsniffer.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=plugins%20portal)](https://plugins.gradle.org/plugin/ru.vyarus.animalsniffer)

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'ru.vyarus:gradle-animalsniffer-plugin:{{ gradle.version }}'
    }
}
apply plugin: 'ru.vyarus.animalsniffer'
```

OR

```groovy
plugins {
    id 'ru.vyarus.animalsniffer' version '{{ gradle.version }}'
}
```

Plugin only works with plugins:

* Jvm languages: `java-base` plugin (activated by any java-related plugin like `java`, `java-library`, `groovy`, `scala`, `org.jetbrains.kotlin.jvm`, etc.).
* Android: `com.android.library` or `com.android.application` plugins (required `7.4.0` or greater)
* Kotlin Multiplatform: `org.jetbrains.kotlin.multiplatform` plugin (required `1.7` or greater). 


[Gradle compatibility matrix](about/compatibility.md)

## Usage

Plugin creates animalsniffer tasks for project sources and assigns them as 
a `check` tasks dependencies, so animalsniffer checks would execute on each build.

## Signatures

AnimalSniffer requires a signature file to check against. To define a signature (or multiple signatures), use
the `signature` configuration.

!!! note
    When **no signatures defined** animalsniffer check tasks **will fail**.  
    This is important to catch common configuration error when signature declared without
    "@signature" qualifier (was often treated as successful execution).

### JDK compatibility

Use java signatures to check older JDK <9 API compatibility. 
For example, java 6 compatibility: 

```groovy
repositories { mavenCentral() }
dependencies {
    signature 'org.codehaus.mojo.signature:java16:1.1@signature'
}
```

[Java signatures](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.codehaus.mojo.signature%22)

!!! note "JDK signatures for java 9 and above"
    JDK 9+ signatures are [not published](https://github.com/mojohaus/animal-sniffer/issues/62):

    ```
    Starting with JDK9+ you can't define a full API signature cause based on the module system you can define your own (limited view on JDK). Apart from that you can use the release configuration in maven-compiler-plugin with JDK9+ to have exactly what animal sniffer offers and that's the reason why there are no JDK9+ signatures.
    ```
    
    To check JDK 9+ compatibility use [--release flag](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:compiling_with_release) instead of plugin
    (or build signatures [manually with maven plugin](https://www.mojohaus.org/animal-sniffer/animal-sniffer-maven-plugin/examples/generating-java-signatures.html)):
    
    ```groovy
    compileJava {
      options.release = 11
    }
    ``` 

### Android compatibility

To check Android API compatibility (for java library or android project):

```groovy
repositories { mavenCentral() }
dependencies {
    signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
}
```

[Android signatures](https://search.maven.org/#search%7Cga%7C1%7Cg%3Anet.sf.androidscents.signature)
(alternative: [gummy bears](https://github.com/open-toast/gummy-bears) (D8 sugar) [signatures list](https://mvnrepository.com/search?q=com.toasttab.android))

### JDK and Android compatibility

To check **both** Java version and Android compatibility, configure both signatures:

```groovy
dependencies {
    signature 'org.codehaus.mojo.signature:java16:1.1@signature'
    signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'
}
```

In this case, animalsniffer will run 2 times: for each signature. You may see the same errors two times if a
class/method is absent in both signatures. Each error message in the log (and file) will also contain the
signature name to avoid confusion.

### Custom signatures

You can also create signatures for any 3rd party library (dependency) to 
[check version compatibility](guide/signature/library.md).

Or build signatures, generated in [your project](guide/signature/build.md) (from project classes) 

!!! tip
    Signature contents [could be viewed](guide/view.md) with a special task.

You can merge and reduce existing signatures to [optimize check time](guide/performance.md).

## Default checks

Animalsniffer tasks created for each "source set" (including tests). By default, tasks for test sources
are disabled - so only "main" sources are checked with the build.

* For java projects tasks created [per source set](guide/use/java.md)
* For android project tasks created [per android component](guide/use/android.md) (variant and test components),
* For kotlin multiplatform tasks created [per platform compilation](guide/use/multiplatform.md) (excluding metadata).

!!! tip
    Use [tasks debug](guide/debug/tasks.md) and [debug option](guide/debug/debug.md) to learn and debug
    animalsniffer tasks configuration.

!!! note
    Even if animalsniffer task is not assigned to `check` task, you can always call it directly
    (animalsniffer tasks are always created for all sources).

### Configure default tasks

By default, all animalsniffer tasks are assigned to the `check` task (except 
tasks for test sources). To exclude animalsniffer tasks from  dependency, use `defaultTargets` option:
just specify required target names (source set, android variant or kotlin compilation)

Null option value (`defaultTargets = null`) means all non-test tasks would be default (defaul value).
Empty value (`defaultTargets = []`) means no default tasks.

See [java](guide/use/java.md#changing-default-tasks), [android](guide/use/android.md#changing-default-tasks)
or [multiplatform](guide/use/multiplatform.md#changing-default-tasks) configuration guides for examples.

!!! important
    Old `animalsniffer.sourceSets` option still exists and would also work, but
    new configuration supersedes it (new property unifies configuration for all project types). 

It is rarely required, but test checks could be enabled with:

```groovy
animalsniffer {
    checkTestSources = true
}
```

## Output

Violations are always printed to console. Example output:

```
2 AnimalSniffer violations were found in 1 files. See the report at: file:///myproject/build/reports/animalsniffer/main.text

[Undefined reference] invalid.(Sample.java:9)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.java:14)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])
```

There are also [text and CSV reports](guide/report.md), enabled by default.

## Suppress violations

A special annotation could be used to [suppress violations](guide/suppress.md)

 

