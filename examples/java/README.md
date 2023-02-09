# Java project with animalsniffer check

Output:

```
> Task :java:compileJava
> Task :java:processResources NO-SOURCE
> Task :java:classes
> Task :java:jar
> Task :java:assemble

> Task :java:animalsnifferMain

> Task :java:compileTestJava NO-SOURCE
> Task :java:processTestResources NO-SOURCE
> Task :java:testClasses UP-TO-DATE
> Task :java:animalsnifferTest UP-TO-DATE
> Task :java:test NO-SOURCE
> Task :java:check
> Task :java:build

BUILD SUCCESSFUL in 646ms
4 actionable tasks: 3 executed, 1 up-to-date

3 AnimalSniffer violations were found in 1 files. See the report at: file:////home/xvik/projects/xvik/gradle-animalsniffer-plugin/examples/java/build/reports/animalsniffer/main.text

[Undefined reference | java16-sun-1.0] invalid.(Sample.java:9)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference | android-api-level-14-4.0_r4] invalid.(Sample.java:9)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference | android-api-level-14-4.0_r4] invalid.(Sample.java:14)
  >> Object javax.naming.InitialContext.doLookup(String)
```