# Signatures build from project jars

```
> Task :buildSignature:fromJars:compileJava NO-SOURCE
> Task :buildSignature:fromJars:processResources NO-SOURCE
> Task :buildSignature:fromJars:classes UP-TO-DATE
> Task :buildSignature:fromJars:jar
> Task :buildSignature:fromJars:assemble
> Task :buildSignature:fromJars:animalsnifferMain NO-SOURCE
> Task :buildSignature:fromJars:compileTestJava NO-SOURCE
> Task :buildSignature:fromJars:processTestResources NO-SOURCE
> Task :buildSignature:fromJars:testClasses UP-TO-DATE
> Task :buildSignature:fromJars:animalsnifferTest NO-SOURCE
> Task :buildSignature:fromJars:test NO-SOURCE
> Task :buildSignature:fromJars:check UP-TO-DATE
> Task :buildSignature:fromJars:animalsnifferSignature

> Task :buildSignature:fromJars:printSignature
Signature mySignature.sig (20.7 Kb) contains 332 classes
	org.junit                      258
	org.hamcrest                   45
	junit.framework                17
	junit.extensions               6
	junit.runner                   3
	junit.textui                   2
	org.codehaus                   1

> Task :buildSignature:fromJars:build

BUILD SUCCESSFUL in 338ms
```