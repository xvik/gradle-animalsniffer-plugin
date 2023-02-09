# Signatures build from other signatures (merge signatures)

```
> Task :buildSignature:fromSignatures:compileJava NO-SOURCE
> Task :buildSignature:fromSignatures:processResources NO-SOURCE
> Task :buildSignature:fromSignatures:classes UP-TO-DATE
> Task :buildSignature:fromSignatures:jar
> Task :buildSignature:fromSignatures:assemble
> Task :buildSignature:fromSignatures:animalsnifferMain
> Task :buildSignature:fromSignatures:compileTestJava NO-SOURCE
> Task :buildSignature:fromSignatures:processTestResources NO-SOURCE
> Task :buildSignature:fromSignatures:testClasses UP-TO-DATE
> Task :buildSignature:fromSignatures:animalsnifferTest
> Task :buildSignature:fromSignatures:test NO-SOURCE
> Task :buildSignature:fromSignatures:check
> Task :buildSignature:fromSignatures:animalsnifferSignature

> Task :buildSignature:fromSignatures:printSignature
Signature mySignature.sig (1.9 Mb) contains 20373 classes
	com.sun                        7115
	javax.swing                    1781
	sun.awt                        771
	java.util                      662
	sun.nio                        640
	sun.security                   632
	java.awt                       561
	org.omg                        538
	javax.xml                      395
	org.apache                     367
	sun.io                         331
	sun.java2d                     259
	javax.management               250
	java.lang                      239
	sun.util                       235
	sun.management                 222
	sun.text                       222
	sun.net                        219
	java.security                  212
	android.widget                 166
	android.provider               160
	android.view                   157
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
	android.text                   120
	javax.naming                   115
	android.graphics               108
	sun.swing                      104
	android.content                103
	javax.security                 99
	sun.print                      96
	android.app                    92
	javax.imageio                  89
	android.renderscript           84
	java.rmi                       77
	java.text                      71
	javax.sound                    71
	android.net                    70
	android.media                  68
	javax.crypto                   63
	sun.applet                     63
	android.os                     62
	org.jcp                        55
	android.database               53
	java.sql                       51
	javax.lang                     48
	javax.net                      48
	javax.sql                      48
	android.test                   44
	android.webkit                 42
	sun.jdbc                       42
	android.util                   41
	org.xml                        39
	android.hardware               27
	javax.accessibility            27
	javax.activation               27
	sun.jkernel                    27
	android                        24
	sun.tools                      24
	android.preference             23
	android.drm                    22
	javax.tools                    22
	android.nfc                    21
	android.opengl                 20
	javax.annotation               19
	android.animation              18
	android.telephony              18
	javax.rmi                      17
	android.bluetooth              16
	javax.smartcardio              16
	android.speech                 14
	javax.jws                      14
	sun.audio                      14
	android.accounts               13
	android.gesture                13
	android.inputmethodservice     13
	javax.microedition             13
	javax.script                   13
	sun.beans                      12
	android.location               11
	sun.dc                         11
	java.math                      10
	junit.framework                10
	org.ietf                       8
	android.sax                    7
	javax.transaction              6
	android.appwidget              5
	android.mtp                    5
	java.applet                    5
	org.json                       5
	org.xmlpull                    5
	android.service                4
	dalvik.system                  4
	sun.corba                      4
	sun.instrument                 4
	android.security               3
	javax.activity                 3
	junit.runner                   3
	android.accessibilityservice   2
	dalvik.annotation              2
	dalvik.bytecode                2
	sunw.util                      2
	com.android                    1
	sunw.io                        1

> Task :buildSignature:fromSignatures:build

BUILD SUCCESSFUL in 1s
```