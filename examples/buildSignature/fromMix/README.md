# Signatures build from different sources

```
> Task :buildSignature:fromMix:compileJava
> Task :buildSignature:fromMix:processResources NO-SOURCE
> Task :buildSignature:fromMix:classes
> Task :buildSignature:fromMix:jar
> Task :buildSignature:fromMix:assemble
> Task :buildSignature:fromMix:animalsnifferMain
> Task :buildSignature:fromMix:compileTestJava NO-SOURCE
> Task :buildSignature:fromMix:processTestResources NO-SOURCE
> Task :buildSignature:fromMix:testClasses UP-TO-DATE
> Task :buildSignature:fromMix:animalsnifferTest NO-SOURCE
> Task :buildSignature:fromMix:test NO-SOURCE
> Task :buildSignature:fromMix:check
> Task :buildSignature:fromMix:animalsnifferSignature

> Task :buildSignature:fromMix:printSignature
Signature mySignature.sig (1.7 Mb) contains 18348 classes
	com.sun                        7115
	javax.swing                    1781
	sun.awt                        771
	java.util                      662
	sun.nio                        640
	sun.security                   632
	java.awt                       561
	org.omg                        538
	javax.xml                      395
	sun.io                         331
	sun.java2d                     259
	javax.management               250
	java.lang                      239
	sun.util                       235
	sun.management                 222
	sun.text                       222
	sun.net                        219
	java.security                  212
	sun.rmi                        157
	javax.print                    156
	java.beans                     155
	sun.misc                       152
	java.nio                       150
	org.w3c                        150
	sun.reflect                    143
	sun.org                        130
	java.io                        126
	java.net                       122
	sun.font                       122
	javax.naming                   115
	sun.swing                      104
	javax.security                 99
	sun.print                      96
	javax.imageio                  89
	java.rmi                       77
	java.text                      71
	javax.sound                    71
	javax.crypto                   63
	sun.applet                     63
	org.jcp                        55
	java.sql                       51
	javax.lang                     48
	javax.net                      48
	javax.sql                      48
	sun.jdbc                       42
	org.xml                        39
	org.slf4j                      34
	javax.accessibility            27
	javax.activation               27
	sun.jkernel                    27
	sun.tools                      24
	javax.tools                    22
	javax.annotation               19
	javax.rmi                      17
	javax.smartcardio              16
	javax.jws                      14
	sun.audio                      14
	javax.script                   13
	sun.beans                      12
	sun.dc                         11
	java.math                      10
	org.ietf                       8
	javax.transaction              6
	java.applet                    5
	sun.corba                      4
	sun.instrument                 4
	javax.activity                 3
	sunw.util                      2
	org.codehaus                   1
	sample                         1
	sunw.io                        1

> Task :buildSignature:fromMix:build

BUILD SUCCESSFUL in 1s
```