# AnimalSniffer plugin usage examples

## Check task examples

All projects intentionally contain errors to demonstrate error reporting

* [Java](java) (also shows validation with android signature (would be the same for other examples))
* [Groovy](groovy)
* [Kotlin](kotlin)
* [Scala](scala)
* [Android library](android-lib)
* [Android application](android-app)

## Android/Kotlin multiplatform full examples

Complete projects, generated with android studio and kotlin init site, with animalsniffer plugin activated
(example of plugin appliance in kotlin gradle scripts)

* [Android](standalone/android-simple) - complete android project
* [Multiplatform: android only](standalone/kotlin-multi-android-only) - 1 multiplatform target
* [Multiplatform: android, desktop, server](standalone/kotlin-multi-android-desktop-server) - several multiplatform targets 
* [Multiplatform: desktop, server](standalone/kotlin-multi-desktop-server) - 2 targets, without android  

## Signature build examples

Examples of building custom signature files for checks

* [Classes](buildSignatire/fromClasses) - signature from project classes
* [Jars](buildSignatire/fromJars) - signature from project jars (configuration)
* [Signatures](buildSignatire/fromSignatures) - signature from other signatures
* [Mixed](buildSignatire/fromMix) - signature from different sources

