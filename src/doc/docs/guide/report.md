# Reports

The plugin supports 3 report types:

1. Console report (always enabled) - simplifies errors resolution
2. Text file - human-readable text report 
3. CSV file (always enabled) - required by plugin internally; could be used for external tool integration

!!! note
    As plugin was written in the same manner as other gradle quality plugins, reports configuration is also the same.

## Console report

Example output:

```
2 AnimalSniffer violations were found in 1 files. See the report at: file:///myproject/build/reports/animalsniffer/main.text

[Undefined reference] invalid.(Sample.java:9)
>> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.java:14)
>> java.nio.file.Path java.nio.file.Paths.get(String, String[])
```

Class in braces `(Sample.java:9)` should be clickable in IDE, and so you could quickly find the place of the problem.

When the problem detected in class field animalsniffer reports field name, but without line:

```java
public class Sample {

    private Path field;
```

```
[Undefined reference] invalid.(Sample.java:1) #field
  >> java.nio.file.Path
```

In this case, the class link would only open the source file (no way to know the exact line).

## Text report

Human-readable text report. The link to text report is printed in the console report, so it could be opened directly.

Example:

```
invalid.Sample:11  Undefined reference: int Boolean.compare(boolean, boolean)
invalid.Sample:16  Undefined reference: java.nio.file.Path java.nio.file.Paths.get(String, String[])
```

Each line is a single error.

As was described above, for class field-related errors, line number would be incorrect:

```
invalid.Sample:1 (#field)  Undefined reference: java.nio.file.Path
```

Text report could be disabled with:

```groovy
tasks.withType(AnimalSniffer) {
    reports.text {
        required = false
    }
}
```

Changed report output:

```groovy
tasks.withType(AnimalSniffer) {
    reports.text {
        outputLocation = file('build/custom.txt')
    }
}
```

## CSV report

The plugin uses CSV report to interchange an error list from animalsniffer (executed by gradle worker)
into a plugin task (console and text report are generated from CSV report).

The report could be useful for external tools integration as it contains easily parsable data.

Example report:

```
java16-sun-1.0;invalid.Sample.java;;field;java.nio.file.Path;false
java16-sun-1.0;invalid.Sample.java;13;;int Boolean.compare(boolean, boolean);false
```

Columns: signature, source file, source line, source field, message itself, true if the error message was not parsed
(this case message column would contain the entire, not parsed message)

It is not possible to disable this report (plugin simply ignores `reports.csv.required` setting).
But the location of CSV file could be configured:

```groovy
tasks.withType(AnimalSniffer) {
    reports.csv {
        outputLocation = file('build/custom.csv')
    }
}
```
