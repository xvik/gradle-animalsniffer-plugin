# Kotlin multiplatform with android example

Output:

```
4 AnimalSniffer violations were found in 2 files. See the report at: file:////home/xvik/projects/xvik/gradle-animalsniffer-plugin/examples/android-kotlin-multiplatform/build/reports/animalsniffer/debug.text

[Undefined reference] invalid.(Sample.kt:11)
  >> int Boolean.compare(boolean, boolean)

[Undefined reference] invalid.(Sample.kt:16)
  >> java.nio.file.Path java.nio.file.Paths.get(String, String[])

[Undefined reference] invalid.(Sample2.kt:10)
  >> java.nio.file.FileSystem java.nio.file.FileSystems.getDefault()

[Undefined reference] invalid.(Sample2.kt:10)
  >> Iterable java.nio.file.FileSystem.getFileStores()

```