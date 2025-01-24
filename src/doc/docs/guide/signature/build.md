# Build project signature

!!! note
    The primary plugin function is signature checking and check tasks are always registered (build is optional).
    To prevent a common configuration problem, check tasks fail when no signatures declared for check. 
    If you use plugin ONLY to build signatures then disable "no-signatures" fail to avoid fails:
    ```groovy
    animalsniffer.failWithoutSignatures = false
    ```

You can build your own signature. For example, to publish it so other projects could
use it for compatibility checks (like with jdk or android).

See [animalsniffer doc](http://www.mojohaus.org/animal-sniffer/animal-sniffer-ant-tasks/examples/generating-signatures.html) for more possible cases.

Signature build task is added only if you configure signature contents:

```groovy
animalsnifferSignature {
    files sourceSets.main.output
}
```

Now, `animalsnifferSignature` task may be called to generate a signature for all 
your project classes.

By default, signature is generated in `build/animalsniffer/signature/${project.name}.sig`
(to be compliant with usual artifacts naming).

The signature may be generated from any combinations of:

* Compiled classes
* Jars
* Other signatures

For example:

```groovy
 dependencies {
    signature 'org.codehaus.mojo.signature:java16-sun:1.0@signature'
    implementation 'junit:junit:4.12'
    implementation "org.codehaus.mojo:animal-sniffer-annotations:1.14"
}

animalsnifferSignature {
    files sourceSets.main.output
    files configurations.compileClasspath

    signatures configurations.signature
}
```

Generated signature will contain jdk6 signature, project classes and classpath jars.
Note that it is not required to use signature configuration to declare signatures:
it was used just for simplicity (signature may be defined from any other source).

`files` and `signatures` configuration methods may be called multiple times.
Methods accept anything that gradle's [`Project.files()`](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#files(java.lang.Object...))
method would accept (File, URI, gradle FileCollection etc).

**Pay attention**: you may use a signature task to merge existing signatures.
[Animalsniffer](http://www.mojohaus.org/animal-sniffer/animal-sniffer-ant-tasks/examples/generating-signatures.html),
used under the hood, does not support this case (empty files), so the plugin
will work around this by adding plugin jar as files with custom exclusion (to exclude plugin classes from generated signature).
Most likely, you will never notice this, as it should be transparent to the end user. It's mentioned just in case
if you look info logs (`-i` gradle option) and note warning message about this.

## Signature customization

You may exclude packages from signature:

```groovy
animalsnifferSignature {
    files sourceSets.main.output
    exclude 'com.mycompany.somepkg.*'
}
```

Everything in `com.mycompany.somepkg` package and subpackages will not be included into generated signature.

Or you may include only classes from some package:

```groovy
animalsnifferSignature {
    files sourceSets.main.output
    include 'com.mycompany.somepkg.*'
}
```

The resulted signature will contain only classes in included package and sub packages.

Both methods may be called multiple times. Multiple packages may be configured at once:

```groovy
include 'com.mycompany.somepkg.*', 'com.mycompany.somepkg2.*'
```

## Signature name

To configure signature name use:

```groovy
animalsnifferSignature {
    files sourceSets.main.output
    outputName = 'mySignature'
}
```

For name without extension, default `.sig` extension will be applied. To override it specify full output name:

```groovy
outputName = 'mySignature.signature'
```

Note that by default output name is the same as a project name.

## Custom task configuration

You may not use configuration and configure build signature task manually:

```groovy
task sig(type: ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask) {
    files sourceSets.main.output
    files configurations.compileClasspath
}
```

Task has the same configuration methods as configuration closure described above.

## 3rd party library

You can also build signature for [any existing jar](library.md)

## Review signature

You may use [SignatureInfoTask](../view.md) to see generated signature content:

```groovy
animalsnifferSignature {
    files sourceSets.main.output
}

task printSignature(type: ru.vyarus.gradle.plugin.animalsniffer.info.SignatureInfoTask) {
    signature = tasks.animalsnifferSignature.outputFiles
    depth = 1
}
```

## Configuration

`animalsnifferConfiguration` closure methods and properties:

| Method | Description |  Example |
|----------|-------------|----------------|
| files | Classes or jars to include into signature | files sourceSets.main.output |
| signatures | Signatures to include ("extend from") | signatures configurations.signature |
| include | Packages to include | include 'some.package.*', 'some.other.*' |
| exclude | Packages to exclude | exclude 'some.package.*', 'some.other.*' |
| outputName | Output signature file name. By default, project name is used. '.sig' extension added when no extenion specified | outputName = 'myName.sig' |
| debug          | Log animalsniffer configuration (useful in case of configuration problems)                                                                                        | false                                           |

`files` and `signatures` are optional, but at least anything must be declared to build signature from.
`files` and `signatures`  accept everything that [Project.files()](https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#files(java.lang.Object...)) will accept
(URI, File, FileCollection etc).

All methods may be called multiple times.