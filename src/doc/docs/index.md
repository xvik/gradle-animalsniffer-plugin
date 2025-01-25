# Welcome to gradle animalsniffer plugin

!!! summary ""
    Use [AnimalSniffer](http://www.mojohaus.org/animal-sniffer/) signatures to check (JDK, android SDK or any library) API compatibility 
    for **java** (**kotlin**, **scala**, **groovy** etc.), **android** and **kotlin multiplatform** projects.

[Release notes](about/release-notes.md) - [History](about/history.md) - [Compatibility](about/compatibility.md) - [License](about/license.md)

Used by:

* [Mockito](https://github.com/mockito/mockito/blob/main/buildSrc/src/main/kotlin/mockito.java-backward-compatibility-checks-conventions.gradle.kts#L27) for java and android compatibility checks
* [Okhttp](https://github.com/square/okhttp/blob/master/okhttp/build.gradle.kts#L254) for java and android compatibility checks (using kotlin multiplatform)
* [Open-telemetry](https://github.com/open-telemetry/opentelemetry-java/blob/main/buildSrc/src/main/kotlin/otel.animalsniffer-conventions.gradle.kts#L10) 
for android compatibility checks (with custom (gammy-bears based) [signatures generation](https://github.com/open-telemetry/opentelemetry-java/blob/main/animal-sniffer-signature/build.gradle.kts))

## Overview

Initially, AnimalSniffer was created to check compatibility with [lower Java versions](https://search.maven.org/search?q=g:org.codehaus.mojo.signature)
(to prevent situations when newer API methods called).

But it's a **general tool**: signatures could be created for [**any library**](guide/signature/library.md)
to check api compatibility against older library versions.
For example, android community [adopted it](https://github.com/open-toast/gummy-bears) for android SDK compatibility verification.

Easy way to **check android SDK compatibility for java library**.

!!! note "JDK signatures for java 9 and above"
    JDK 9+ signatures are [not published](https://github.com/mojohaus/animal-sniffer/issues/62) due to module nature: use [--release flag](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:compiling_with_release) instead of plugin
    
    ```groovy
    compileJava {
      options.release = 11
    }
    ```

    Use plugin for java 8 and before checks, for android or any other library signatures verification.

## Features

* Works with:
    - Java (and related) plugins: **java** (java-library), **kotlin**, **groovy** (only with `@CompileStatic`), **scala**, 
      or any other jvm language (which plugin use sourceSets)
    - **Android** projects (library and application plugins)
    - **Kotlin multiplatform** projects
* Could create signatures for [your](guide/signature/build.md) or any [3rd party](guide/signature/library.md) 
  library (to check compatibility against the older library version)
* Special task to [view signature](guide/view.md) file contents
* [Report](guide/report.md) errors to console (to simplify reaction), text file and CSV (for external tools)
* Could merge existing signatures (e.g. for [caching](guide/performance.md))
* Compatible with gradle configuration cache (gradle 9+ requirement)
* [Debug](guide/debug/debug.md) tools to learn or check configuration

## Example projects

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