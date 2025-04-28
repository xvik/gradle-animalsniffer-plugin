# Check JVM language

For JVM languages (java, groovy, kotlin, scala, etc) which plugin is based `java` plugin 
(based on source sets), animalsniffer check tasks registered **per source set**.

In the simplest case:

```groovy
plugins {
    id 'java' // kotlin, scala, groovy
    id 'ru.vyarus.animalsniffer' version "{{ gradle.version }}"
}

dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
}
```

Part of [print sources task](../debug/sources.md) output:

```
== [SourceSets] ===============================================================

	Java Source Sets ------------------------------------------------------- (2)

		main -----

			Sources
				src/main/java

			Output
				build/classes/java/main
				build/resources/main

		test -----

			Sources
				src/test/java                                                                    NOT EXISTS

			Output
				build/classes/java/test
				build/resources/test

```

We have 2 source sets: 'main' and 'test' and so two animalsniffer check tasks created:
`animlasnifferMain` and `animalsnifferTest`.

Due to `animalsniffer.checkTestSources = false` (by default) only `animalsnifferMain` is attached to check
and so would be executed with each build.

This could also be seen with [print animalsniffer tasks](../debug/tasks.md) task:

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


	animalsnifferTest                                   for 'test' source set
		report: build/reports/animalsniffer/test.text
		depends on: testClasses
		signatures: 
			java16-sun-1.0.signature
		classes:
			build/classes/java/test
		sources:
			src/test/java                                                                    NOT EXISTS
```

!!! note
    `[default]` indicates tasks executed with build

!!! tip
    Use [`animalsniffer.debug = true`](../debug/debug.md) to see class files, checked by animalsniffer task

## Changing default tasks

For example, suppose we have two more source sets:

```groovy
sourceSets {
    other {
        java { srcDir("src/other/java")}
    }
    integrationTests {
        java { srcDir("src/itest/java")}
    }
}
```

Then, by default, two animalsniffer tasks would be attached to `check`: `animalsnifferMain`, `animalsnifferOther`.
If you want to exclude task for "other" source set:

```groovy
animalsniffer {
    defaultTargets = ['main']
}
```

or with method

```groovy
animalsniffer {
    defaultTargets 'main'
}
```

!!! tip
    If you want to avoid all animalsniffer tasks:
    ```groovy
    defaultTargets = []
    ```

### Deprecated config

Animalsniffer plugin was initially written in the same manner as other gradle quality plugins (checkstyle, pmd etc.)
All these plugins are configured with `sourceSets` option. Plugin could use this configuration too:

```groovy
animalsniffer {
    sourceSets = [sourceSets.main]
}
```

But this configuration is considered **DEPRECATED** because it is clumsy (especially in kotlin) and
plugin now supports android and kotlin multiplatform, which are not based on source sets.

Strings-based `defaultTargets` configuration (presented above) is universal for all types of projects 
and should be more comfortable to use

### Test tasks

Test sources are not checked by default. If you want to check test sources:

```groovy
animalsniffer {
    checkTestSources = true
}
```

After that `defaultTargets` option could be used to limit test tasks. 
