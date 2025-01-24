# Debug option

If you're not sure that animalsniffer check or build task is correctly configured, 
use debug mode:

```groovy
animalsniffer {
    debug = true
}
```

After activation, it would print task configuration just before execution.

!!! note
    Debug info will not appear if animalsniffer task skipped (e.g. due to no classes to check)

## Check

Example for the check task in a simple java project:

```
> Task :animalsnifferMain

	signatures:
		java16-sun-1.0.signature

	sources:
		src/main/java

	files:
		build/classes/java/main/valid/Sample.class
```

Here you can see:

* Applied signatures
* Source directories
* All checked class files

Another example - check task in android project:

```
> Task :animalsnifferDebug

	signatures:
		java18-1.0.signature
		android-api-level-21-5.0.1_r2.signature

	sources:
		build/generated/aidl_source_output_dir/debug/out
		build/generated/renderscript_source_output_dir/debug/out
		build/generated/source/buildConfig/debug
		src/debug/java
		src/debug/kotlin
		src/main/java
		src/main/kotlin

	files:
		build/intermediates/javac/debug/classes/com/example/namespace/BuildConfig.class
		build/intermediates/javac/debug/classes/invalid/Sample.class
```

## Build

Example output for build signature task:

```
> Task :animalsnifferSignature
spock_Check_include_and_e_0_testProjectDir16553270069100638944.sig

	files:
		build/classes/java/main
		build/resources/main

	include:
		valid.*

	exclude:
		invalid.*
```

Different options might be shown (depends on task configuration):

1. Output signature name (at the top)
2. Source signature files
3. Source classes (dirs)
4. Include patterns
5. Exclude patterns