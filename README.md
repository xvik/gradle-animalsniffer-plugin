# gradle-animalsniffer-plugin
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](http://www.opensource.org/licenses/MIT)
[![CI](https://github.com/xvik/gradle-animalsniffer-plugin/actions/workflows/CI.yml/badge.svg)](https://github.com/xvik/gradle-animalsniffer-plugin/actions/workflows/CI.yml)
[![Appveyor build status](https://ci.appveyor.com/api/projects/status/github/xvik/gradle-animalsniffer-plugin?svg=true)](https://ci.appveyor.com/project/xvik/gradle-animalsniffer-plugin)
[![codecov](https://codecov.io/gh/xvik/gradle-animalsniffer-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/xvik/gradle-animalsniffer-plugin)

**DOCUMENTATION** https://xvik.github.io/gradle-animalsniffer-plugin

### About

Use [AnimalSniffer](http://www.mojohaus.org/animal-sniffer/) signatures to check (JDK, android SDK or any library) API compatibility
for **java** (**kotlin**, **scala**, **groovy** etc.), **android** and **kotlin multiplatform** projects.

General tool: signatures could be created for any library to check api compatibility against older library versions.
Android community [adopted it](https://github.com/open-toast/gummy-bears) for android SDK compatibility verification.

Easy way to **check android SDK compatibility for java library**.

**NOTE**: Initially, animalsniffer was created for java compatibility checks, but java signatures 
for JDK 9+ are [not published](https://github.com/mojohaus/animal-sniffer/issues/62) due to module nature: use 
[--release flag](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:compiling_with_release)
to check JDK 9+ backwards compatibility

```groovy
compileJava {
  options.release = 11
}
```

Features:

* Works with:
  - Java (and related) plugins: **java** (java-library), **kotlin**, **groovy** (only with `@CompileStatic`), **scala**,
    or any other jvm language (which plugin use sourceSets)
  - **Android** projects (library and application plugins)
  - **Kotlin multiplatform** projects
* Could create signatures for [your](https://xvik.github.io/gradle-animalsniffer-plugin/latest/guide/signature/build) or any 
[3rd party](https://xvik.github.io/gradle-animalsniffer-plugin/latest/guide/signature/library)
  library (to check compatibility against the older library version)
* Special task to [view signature](https://xvik.github.io/gradle-animalsniffer-plugin/latest/guide/view) file contents
* [Report](https://xvik.github.io/gradle-animalsniffer-plugin/latest/guide/report) errors to console (to simplify reaction), text file and CSV (for external tools)
* Could merge existing signatures (e.g. for [caching](https://xvik.github.io/gradle-animalsniffer-plugin/latest/guide/performance))
* Compatible with gradle configuration cache (gradle 9+ requirement)
* [Debug](https://xvik.github.io/gradle-animalsniffer-plugin/latest/guide/debug/debug) tools to learn or check configuration

Used by:

* [Mockito](https://github.com/mockito/mockito/blob/main/buildSrc/src/main/kotlin/mockito.java-backward-compatibility-checks-conventions.gradle.kts#L27) for java and android compatibility checks
* [Okhttp](https://github.com/square/okhttp/blob/master/okhttp/build.gradle.kts#L254) for java and android compatibility checks (using kotlin multiplatform)
* [Open-telemetry](https://github.com/open-telemetry/opentelemetry-java/blob/main/buildSrc/src/main/kotlin/otel.animalsniffer-conventions.gradle.kts#L10)
  for android compatibility checks (with custom (gammy-bears based) [signatures generation](https://github.com/open-telemetry/opentelemetry-java/blob/main/animal-sniffer-signature/build.gradle.kts))

##### Summary

* Configuration extensions: 
    - `animalsniffer` - check configuration
    - `animalsnifferSignature` - signature [build configuration](https://xvik.github.io/gradle-animalsniffer-plugin/latest/guide/signature/build) (optional)
* Tasks:
    - `animalsniffer[Target]` - check "[target]" sources (source set, android variant or kotlin platform) 
    - `animalsnifferSignature` - build signature (active when `animalsnifferSignature` configuration declared)
    - `printAnimalsnifferTasks` - print registered check tasks
    - `printAnimalsnifferSourceInfo` - print defailed project sources and compile tasks info (for debug reports)
    - `type:BuildSignatureTask` - custom signature build task may be used to [merge signatures](https://xvik.github.io/gradle-animalsniffer-plugin/latest/guide/signature/library)
    - `type:SignatureInfoTask` - view signature ["contents"](https://xvik.github.io/gradle-animalsniffer-plugin/latest/guide/view/)  
* Dependencies configuration: `signature` - signatures for check    

### Setup

[![Maven Central](https://img.shields.io/maven-central/v/ru.vyarus/gradle-animalsniffer-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/ru.vyarus/gradle-animalsniffer-plugin)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/ru/vyarus/animalsniffer/ru.vyarus.animalsniffer.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=plugins%20portal)](https://plugins.gradle.org/plugin/ru.vyarus.animalsniffer)


```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'ru.vyarus:gradle-animalsniffer-plugin:2.0.0'
    }
}
apply plugin: 'ru.vyarus.animalsniffer'
```

OR 

```groovy
plugins {
    id 'ru.vyarus.animalsniffer' version '2.0.0'
}
```

#### Compatibility

Plugin only works with plugins:

* Jvm languages: `java-base` plugin (activated by any java-related plugin like `java`, `java-library`, `groovy`, `scala`, `org.jetbrains.kotlin.jvm`, etc.).
* Android: `com.android.library` or `com.android.application` plugins (required `7.4.0` or greater)
* Kotlin Multiplatform: `org.jetbrains.kotlin.multiplatform` plugin (required `1.7` or greater).


The plugin is compiled for Java 8 and tested for compatibility with Java 11, 17 and 21.

Gradle | Version
--------|-------
7-8     | [2.0.0](https://xvik.github.io/gradle-animalsniffer-plugin/2.0.0) 
5       | [1.7.2](https://github.com/xvik/gradle-animalsniffer-plugin/tree/1.7.2)
4.x     | [1.4.6](https://github.com/xvik/gradle-animalsniffer-plugin/tree/1.4.6)

#### Snapshots

<details>
      <summary>Snapshots may be used through JitPack or GitHub packages</summary>

##### GitHub package

WARNING: Accessing GitHub package requires [GitHub authorization](https://docs.github.com/en/enterprise-cloud@latest/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-to-github-packages)!

Access token creation is [described here](https://blog.vyarus.ru/using-github-packages-in-gradle-and-maven-projects)

* Add to `settings.gradle` (top most!) (exact commit hash might be used as version) :

  ```groovy
  pluginManagement {
      repositories {
          gradlePluginPortal()      
          maven { 
              url = 'https://maven.pkg.github.com/xvik/gradle-animalsniffer-plugin'
              credentials {
                 username = settings.ext.find('gpr.user') ?: System.getenv("USERNAME")
                 password = settings.ext.find('gpr.key') ?: System.getenv("TOKEN")
            } 
          }             
      }
  }    
  ```   
* In global gradle file `~/.gradle/gradle.properties` add
  ```
  gpr.user=<your github user name>
  gpr.key=<your github password or classic token>
  ```                                            
  (or credentials must be declared in environment: USERNAME/TOKEN (more usable for CI))
  Read [personal access tokens creation guide](https://docs.github.com/en/enterprise-cloud@latest/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic)
  Note that token needs only "package/read" permission 
* Use plugin snapshot (see [the latest published package version](https://github.com/xvik/gradle-animalsniffer-plugin/packages/2339003)):

  ```groovy
  plugins {
      id 'ru.vyarus.animalsniffer' version '2.0.1-SNAPSHOT'
  }
  ```  

##### JitPack

* Add to `settings.gradle` (top most!) (exact commit hash might be used as version) :

  ```groovy
  pluginManagement {
      resolutionStrategy {
          eachPlugin {
              if (requested.id.id == 'ru.vyarus.use-python') {
                  useModule('ru.vyarus:gradle-animalsniffer-plugin:master-SNAPSHOT')
              }
          }
      }
      repositories {
          gradlePluginPortal()      
          maven { url 'https://jitpack.io' }              
      }
  }    
  ```  
  Note: this would use the latest snapshot. To use exact commit version, go to
  [JitPack project page](https://jitpack.io/#ru.vyarus/gradle-animalsniffer-plugin)
* Use plugin without declaring version:

  ```groovy
  plugins {
      id 'ru.vyarus.animalsniffer'
  }
  ```

</details>


### Usage

Read [documentation](https://xvik.github.io/gradle-animalsniffer-plugin/)

### Example projects

Check:

* [Java](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/java) (including android signatures)
* [Groovy](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/groovy)
* [Kotlin](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/kotlin)
* [Scala](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/scala)
* [Android library](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/android-lib)
* [Android application](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/android-app)
* [Multiplatform](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/kotlin-multiplatform)
* [Multiplatform + android](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/android-kotlin-multiplatform)

Signature build:

* [Classes](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/buildSignatire/fromClasses) - signature from project classes
* [Jars](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/buildSignatire/fromJars) - signature from project jars (configuration)
* [Signatures](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/buildSignatire/fromSignatures) - signature from other signatures
* [Mixed](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/buildSignatire/fromMix) - signature from different sources

Full android and multiplatform projects (created with android studio and kotlin init site):

* [Android](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/standalone/android-simple) - complete android project
* [Multiplatform: android only](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/standalone/kotlin-multi-android-only) - 1 multiplatform target
* [Multiplatform: android, desktop, server](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/standalone/kotlin-multi-android-desktop-server) - several multiplatform targets
* [Multiplatform: desktop, server](https://github.com/xvik/gradle-animalsniffer-plugin/tree/master/examples/standalone/kotlin-multi-desktop-server) - 2 targets, without android


### Might also like

* [quality-plugin](https://github.com/xvik/gradle-quality-plugin) - java and groovy source quality checks
* [mkdocs-plugin](https://github.com/xvik/gradle-mkdocs-plugin) - project documentation generator
* [pom-plugin](https://github.com/xvik/gradle-pom-plugin) - improves pom generation
* [java-lib-plugin](https://github.com/xvik/gradle-java-lib-plugin) - avoid boilerplate for java or groovy library project
* [github-info-plugin](https://github.com/xvik/gradle-github-info-plugin) - pre-configure common plugins with github related info
* [java-library generator](https://github.com/xvik/generator-lib-java) - java library project generator

---
[![gradle plugin generator](http://img.shields.io/badge/Powered%20by-%20Gradle%20plugin%20generator-green.svg?style=flat-square)](https://github.com/xvik/generator-gradle-plugin) 
