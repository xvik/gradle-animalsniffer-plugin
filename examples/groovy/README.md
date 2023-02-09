# Groovy project with animalsniffer check

Output:

```
> Task :groovy:compileJava NO-SOURCE
> Task :groovy:compileGroovy
> Task :groovy:processResources NO-SOURCE
> Task :groovy:classes
> Task :groovy:jar
> Task :groovy:assemble

> Task :groovy:animalsnifferMain

> Task :groovy:compileTestJava NO-SOURCE
> Task :groovy:compileTestGroovy NO-SOURCE
> Task :groovy:processTestResources NO-SOURCE
> Task :groovy:testClasses UP-TO-DATE
> Task :groovy:animalsnifferTest UP-TO-DATE
> Task :groovy:test NO-SOURCE
> Task :groovy:check
> Task :groovy:build

BUILD SUCCESSFUL in 1s
4 actionable tasks: 3 executed, 1 up-to-date

2 AnimalSniffer violations were found in 1 files. See the report at: file:////home/xvik/projects/xvik/gradle-animalsniffer-plugin/examples/groovy/build/reports/animalsniffer/main.text

[Undefined reference] invalid.(Sample.groovy:13)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.groovy:18)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])
```