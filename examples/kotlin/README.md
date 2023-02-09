# Kotlin project with animalsniffer check

Output:

```
> Task :kotlin:processResources NO-SOURCE
> Task :kotlin:processTestResources NO-SOURCE
> Task :kotlin:compileKotlin
> Task :kotlin:compileJava NO-SOURCE
> Task :kotlin:classes UP-TO-DATE
> Task :kotlin:inspectClassesForKotlinIC
> Task :kotlin:jar
> Task :kotlin:assemble

> Task :kotlin:animalsnifferMain

> Task :kotlin:compileTestKotlin NO-SOURCE
> Task :kotlin:compileTestJava NO-SOURCE
> Task :kotlin:testClasses UP-TO-DATE
> Task :kotlin:animalsnifferTest UP-TO-DATE
> Task :kotlin:test NO-SOURCE
> Task :kotlin:check
> Task :kotlin:build

BUILD SUCCESSFUL in 950ms
5 actionable tasks: 4 executed, 1 up-to-date

2 AnimalSniffer violations were found in 1 files. See the report at: file:////home/xvik/projects/xvik/gradle-animalsniffer-plugin/examples/kotlin/build/reports/animalsniffer/main.text

[Undefined reference] invalid.(Sample.kt:11)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.kt:16)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])
```