# View signature content

A special task is provided to be able to view signature content `SignatureInfoTask`.

Task supposed to be used for:

* Curiosity: review default signatures content
* Check: review build signature (after build signature task)
* Optimize: review and optimize cache signature size to speed up check task

## JDK signature

For example, to look java6 signature contents use:

```groovy
dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
}

task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
    signature = configurations.signature
    depth = 2
}
```

It will print:

```
Signature java16-sun-1.0.signature (1.7 Mb) contains 18312 classes
	com.sun                        7115
	javax.swing                    1781
	sun.awt                        771
	java.util                      662
	sun.nio                        640
	sun.security                   632
	...
```

Here you can see that java6 signature (1.7mb file) contains 18312 classes. Different
depth values could be used to build different views. For example, with depth = 1:

```
Signature java16-sun-1.0.signature (1.7 Mb) contains 18312 classes
	com                  7115
	sun                  4636
	javax                3327
	java                 2441
	org                  790
	sunw                 3
```

### Sorting by name

If you want to see alphabetical packages order, then disable sorting by size:

```groovy
task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
    signature = animalsnifferCacheMain.outputFiles
    depth = 2
    sortBySize = false
}
```

```
Signature java16-sun-1.0.signature (1.7 Mb) contains 18312 classes
	com.sun                        7115
	java.applet                    5
	java.awt                       561
	java.beans                     155
	java.io                        126
	java.lang                      239
```

This may be handy for educational purposes.

## Project signature

Checking just created project signature:

```groovy
animalsnifferSignature {
    files sourceSets.main.output
}

task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
    signature = tasks.animalsnifferSignature.outputFiles
    depth = 1
}
```

## Cache signature

Checking signature caches for check task:

```groovy
animalsniffer {
   cache.enabled = true
}

task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
    signature = cacheAnimalsnifferMainSignatures.outputFiles
    depth = 2
    sortBySize = false
}
```

When multiple signatures used for the check, multiple info messages will be printed:

```groovy
dependencies {
   signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
   signature 'net.sf.androidscents.signature:android-api-level-14:4.0_r4@signature'                
   implementation 'org.slf4j:slf4j-api:1.7.25'
}
```

```
Signature animalsnifferMainCache_!java16-sun-1.0.sig (1.3 Mb) contains 13710 classes
	com.sun                        7115
	javax.swing                    1781
	java.util                      662
	java.awt                       561
	org.omg                        538
	javax.xml                      395
	javax.management               250
	java.lang                      239
	java.security                  212
	javax.print                    156
	java.beans                     155
	java.nio                       150
	org.w3c                        150
	java.io                        126
	java.net                       122
	javax.naming                   115
	javax.security                 99
	javax.imageio                  89
	java.rmi                       77
	java.text                      71
	javax.sound                    71
	javax.crypto                   63
	org.jcp                        55
	java.sql                       51
	javax.lang                     48
	javax.net                      48
	javax.sql                      48
	org.xml                        39
	org.slf4j                      34
	javax.accessibility            27
	javax.activation               27
	javax.tools                    22
	javax.annotation               19
	javax.rmi                      17
	javax.smartcardio              16
	javax.jws                      14
	javax.script                   13
	java.math                      10
	org.ietf                       8
	javax.transaction              6
	java.applet                    5
	javax.activity                 3
	sunw.util                      2
	sunw.io                        1
Signature animalsnifferMainCache_!android-api-level-14-4.0_r4.sig (317.1 Kb) contains 3167 classes
	org.apache                     367
	java.util                      229
	java.security                  173
	android.widget                 166
	android.provider               160
	android.view                   157
	java.lang                      132
	android.text                   120
	android.graphics               108
	android.content                103
	android.app                    92
	android.renderscript           84
	java.io                        80
	android.net                    70
	java.nio                       69
	android.media                  68
	android.os                     62
	java.net                       60
	javax.xml                      54
	android.database               53
	java.sql                       48
	android.test                   44
	android.webkit                 42
	javax.crypto                   42
	android.util                   41
	javax.net                      39
	org.w3c                        36
	org.xml                        35
	org.slf4j                      34
	java.text                      30
	android.hardware               27
	android                        24
	android.preference             23
	android.drm                    22
	android.nfc                    21
	android.opengl                 20
	javax.security                 19
	android.animation              18
	android.telephony              18
	android.bluetooth              16
	javax.sql                      15
	android.speech                 14
	android.accounts               13
	android.gesture                13
	android.inputmethodservice     13
	javax.microedition             13
	android.location               11
	junit.framework                10
	android.sax                    7
	android.appwidget              5
	android.mtp                    5
	java.beans                     5
	org.json                       5
	org.xmlpull                    5
	android.service                4
	dalvik.system                  4
	java.math                      4
	android.security               3
	junit.runner                   3
	android.accessibilityservice   2
	dalvik.annotation              2
	dalvik.bytecode                2
	java.awt                       2
	com.android                    1
```