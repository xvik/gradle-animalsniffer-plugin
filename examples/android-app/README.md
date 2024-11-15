# Android library project with animalsniffer check

Output:

```
> Task :android-app:animalsnifferDebug

4 AnimalSniffer violations were found in 2 files. See the report at: file:////{PATH_FROM_ROOT}/gradle-animalsniffer-plugin/examples/android-app/build/reports/animalsniffer/debug.text

[Undefined reference | java18-1.0] invalid.(Sample.java:9)
  >> String String.repeat(int)

[Undefined reference | android-api-level-21-5.0.1_r2] invalid.(Sample.java:9)
  >> String String.repeat(int)

[Undefined reference | android-api-level-21-5.0.1_r2] invalid.(Sample.java:15)
  >> java.nio.file.Path java.io.File.toPath()

[Undefined reference | android-api-level-21-5.0.1_r2] invalid.(SampleKotlin.kt:9)
  >> java.nio.file.Path java.io.File.toPath()
```