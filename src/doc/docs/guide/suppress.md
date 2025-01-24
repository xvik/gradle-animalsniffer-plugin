# Suppress violations

Special annotation could be used to suppress violations:
[examples](http://www.mojohaus.org/animal-sniffer/animal-sniffer-annotations/index.html)

## Default annotation

Add dependency on the annotation artifact:

```groovy
implementation "org.codehaus.mojo:animal-sniffer-annotations:{{ gradle.animalsnifferVersion }}"
``` 

Use `provided` scope if you can.
Annotation is configured by default, so you can simply use annotation to suppress violation:

```groovy
@IgnoreJRERequirement
private Optional param;
```

## Custom annotation

You can define your own annotation:

```groovy
package com.mycompany

@Retention(RetentionPolicy.CLASS)
@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface SuppressSignatureCheck {}
```

Configure annotation:

```groovy
animalsniffer {
    annotation = 'com.mycompany.SuppressSignatureCheck'
}
```

Now check will skip blocks annotated with your annotation:

```groovy
@SuppressSignatureCheck
private Optional param;
```

## Ignore classes

Your project could target multiple Java versions and so reference classes, not present in a signature.

For example, your implementation could try to use Java 7 `Paths` and if the class is not available, fall back
to the Java 6 implementation. In this case `Paths` could be added to the ignored classes:

```groovy
animalsniffer {
    ignore 'java.nio.file.Paths'
}
``` 

Now usages of `Paths` will not cause warnings.

Multiple ignored classes could be defined:

```groovy
animalsniffer {
    ignore 'java.nio.file.Paths', 'some.other.Class'
}
```

Or

```groovy
animalsniffer {
    ignore 'java.nio.file.Paths'
    ignore 'some.other.Class'
}
```

Or by directly assigning collection:

```groovy
animalsniffer {
    ignore  = ['java.nio.file.Paths', 'some.other.Class']
}
```

Entire packages could be ignored using asterisk:

```groovy
animalsniffer {
    ignore 'some.pkg.*'
}
```

See more info in
[the documentation](http://www.mojohaus.org/animal-sniffer/animal-sniffer-ant-tasks/examples/checking-signatures.html#Ignoring_classes_not_in_the_signature).
