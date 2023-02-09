# Scala project with animalsniffer check

Output:

```
> Task :scala:compileJava NO-SOURCE

> Task :scala:compileScala
[Warn] /home/xvik/projects/xvik/gradle-animalsniffer-plugin/examples/scala/src/main/scala/invalid/Sample.scala:8:8: Sample has a main method with parameter type Array[String], but invalid.Sample will not be a runnable program.
  Reason: main method must have exact signature (Array[String])Unit
one warning found

> Task :scala:processResources NO-SOURCE
> Task :scala:classes
> Task :scala:jar
> Task :scala:assemble

> Task :scala:animalsnifferMain

> Task :scala:compileTestJava NO-SOURCE
> Task :scala:compileTestScala NO-SOURCE
> Task :scala:processTestResources NO-SOURCE
> Task :scala:testClasses UP-TO-DATE
> Task :scala:animalsnifferTest UP-TO-DATE
> Task :scala:test NO-SOURCE
> Task :scala:check
> Task :scala:build

BUILD SUCCESSFUL in 3s
4 actionable tasks: 3 executed, 1 up-to-date

2 AnimalSniffer violations were found in 1 files. See the report at: file:////home/xvik/projects/xvik/gradle-animalsniffer-plugin/examples/scala/build/reports/animalsniffer/main.text

[Undefined reference] invalid.(Sample.scala:12)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.scala:17)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])
```