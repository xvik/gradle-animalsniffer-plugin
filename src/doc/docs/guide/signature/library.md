# Dependency signatures (library)

Animalsniffer could be used to check compatibility with 3rd party libraries.

## Example

The example is very synthetic but should clearly show how to use signatures for checking compatibility with libraries.

### Complete signature

Suppose you're using in your project slf4j-api 1.7.25 and want to be sure that you're also compatible with 1.5.3.
We can generate a signature for slf4j 1.5.3 and use it to validate compatibility.

```groovy
plugins {
    id 'java'
    id 'ru.vyarus.animalsniffer'
}

// use custom configuration to build signature            
configurations.create('newsig')

task sig(type: ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask) {
    // (optional) build slf4j signature as an extension to jdk signature
    signatures configurations.signature
    files configurations.newsig
}

animalsniffer {
    // use generated signature instead of configuration
    signatures = sig.outputFiles
    excludeJars 'slf4j-*'
}                        

repositories { mavenCentral()}
dependencies {
    // this signature is used only to build custom signature, but not in check directly
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
    // dependency that must to be excluded to be able to check with newly generated signature
    implementation 'org.slf4j:slf4j-api:1.7.25'
                
    // configuration used only to build signature
    newsig 'org.slf4j:slf4j-api:1.5.3'
}
```

Here we use custom configuration to get old slf4j version and build jdk+old slf4j signature with the custom task.
Custom signature is used for check.

**NOTE** that it is important to exclude library jar, otherwise signature will not detect anything (real jar will mark all usages as valid).

If we run check on class:

```java
public class Sample {
    public static void main(String[] args) {
        // api present in 1.5.3 (line must not be errored)
        LoggerFactory.getLogger("goodapi");
        // method appear in 1.5.4
        MarkerFactory.getMarker("sample").hasReferences();
    }
}
```

It will detect api change:

```
[Undefined reference] custsig.(Sample.java:12)
  >> boolean org.slf4j.Marker.hasReferences()
```

### Partial signature

Most likely, small library signature would be built without jdk (library signature only). In such case, we would
need to merge our required jdk signature with small library signature (it is required because if we use just a small signature, all jdk classes usages will be treated as violations).

Note that you may base on android signature or use library signature in both android and java projects. This makes small signature re-usable.

Cache task is not merging signatures by default because commonly complete signatures are used (jdk, android) and merge is not desired. But it can merge signatures. Modified first example:

```groovy
plugins {
    id 'java'
    id 'ru.vyarus.animalsniffer'
}

// creating signature just for slf4j library
configurations.create('newsig')
task sig(type: ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask) {
    files configurations.newsig
}

animalsniffer {
    // using both signatures for check
    signatures = files(configurations.signature, sig.outputFiles)
    excludeJars 'slf4j-*'
    cache {
        enabled = true

        // cache would merge signatures into single signature
        mergeSignatures = true
    }
}                        

repositories { mavenCentral()}
dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
    // dependency that must to be excluded to be able to check with newly generated signatire
    implementation 'org.slf4j:slf4j-api:1.7.25'
    
    // configuration used only to build signature
    newsig 'org.slf4j:slf4j-api:1.5.3'
}
```

If library signature is published into maven repository, the configuration will become:

```groovy
plugins {
    id 'java'
    id 'ru.vyarus.animalsniffer'
}

animalsniffer {
    excludeJars 'slf4j-*'
    cache {
        enabled = true
        mergeSignatures = true
    }
}                        

repositories { mavenCentral()}
dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
    // note this is not real, but just to show the idea - all signatures configured in one place
    signature 'org.slf4j:slf4j-api:1.5.3@signature'

    implementation 'org.slf4j:slf4j-api:1.7.25'
}
```

### Partial without cache

In some situations, the cache could not be used.

For example, if you want to check with both jdk and android and need to use library 
signature. In this case, cache would merge everything, which is not desirable. 
For such cases, use custom signature tasks to build custom signatures 
(as in the first example).

Another caveat is possible [signature build conflict](../performance.md). In this case, custom build tasks would also help.

## Configuration

### Jar exclude

As shown above, you need to exclude jars, covered with library signatures.

```groovy
animalsniffer {
    excludeJars 'slf4j-*'
}
```

Excluding occurs on classpath jars. Jar files are matched without extension. Convention for classpath file names is `artifactId-version`.

For example, slf4j jar would be `slf4j-api-1.5.3.jar`, but pattern would be matched with `slf4j-api-1.5.3`.

Patterns are actually regular expressions, but in most cases this is not required and that's why '*' symbol is supported: it is replaced to '.+'. By definition above, `slf4j-.+` regexp will be used for matching.

Many patterns could be declared:

```groovy
excludeJars 'slf4j-*', 'some-*'
```

Configuration method could be called multiple times (addition):

```groovy
excludeJars 'slf4j-*', 'some-*'
excludeJars 'other-*'
```

Property may be configured directly, but it would override previous values:

```groovy
excludeJars = ['slf4j-*', 'some-*']
```

### Signatures

Use [project.files()](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#files(java.lang.Object...)) to group multiple file sets (or specify separate files, url etc).

```groovy
animalsniffer {
    signatures = files(configurations.signature, sig.outputFiles)
}
```

Note that working with FileCollection allows evaluating files lazily. In the example 
above, `sig.outputFiles` files do not exist in the configuration time, but as 
it's a lazy collection, files being resolved correctly. Moreover, using this method 
puts implicit depdendency on task. `project.files()` preserve laziness when merging 
multiple collections.

!!! note
    Do not rely on build task outputs, because task declares only output directory 
    and `task.outputs.files` will contain ONLY directory itself. 
    Use the special method instead: `task.getOutputFiles()` 