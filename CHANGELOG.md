* (breaking) Drop gradle 5 and 6 support
* Add android projects support (activates for android library and application plugins
   and use variants (debug, release) instead of source sets)
* Add androidVariants configuration option to use instead of sourceSets in android projects
  (to define which animalsniffer tasks would run with check task (together with build))
* Add debugAnimalsnifferSources task for debug purposes (shows registered source sets, tasks, plugins)
* Add ability to declare target (java) source sets with strings  
  (new method in extension: animalsniffer.sourceSets 'main', 'other')

### 1.7.2 (2024-11-18)
* Update animalsniffer 1.23 -> 1.24
* Fix gradle 8.11 support (#100)
* Fix file report disabling
* Add AnimalSniffer task shortcut for simpler reports configuration

### 1.7.1 (2023-07-05)
* Update animalsniffer 1.22 -> 1.23
* Plugin requires now JavaBasePlugin instead of JavaPlugin.
  Required for kotlin multiplatform 1.9.20 activating only base plugin with jvm().withJava() (#84)

NOTE: animalsniffer 1.23 reports problems on fields without line number (but with field name),
Plugin output would contain line 1 (default for not defined line)

### 1.7.0 (2023-02-09)
* Support animalsniffer messages for field violations (#25)
* Always put line number in file report, even if it wasn't declared (consistency with console reporting)
* Fix gradle deprecation warning (#67)
* Skip check task when no signatures configured or no files to check (to differentiate with success execution 
  and easily spot configuration problems)
* Add custom task shortcuts: allow custom build signature and signature info tasks declaration without task package
* Add debug output for check and build tasks (to simplify configuration debugging)
  - animalsniffer.debug = true for check tasks
  - animalsnifferSignature.debug = true for signature build task

### 1.6.0 (2022-08-20)
* Update animalsniffer 1.20 -> 1.22 (java 9 support)
* Fix configuration cache support for check tasks (#26)
  - Type of sourcesDirs property of AnimalSniffer tasks changed, but it should not be a problem
* Fix formatting messages without source line number
* Remove duplicate animalsniffer messages without line number

### 1.5.4 (2021-11-06)
* Fix gradle 7 deprecation warnings 

### 1.5.3 (2021-02-18)
* Update animalsniffer 1.18 -> 1.20 (asm 9; java 8 minimum)

### 1.5.2 (2020-11-05)
* Fix inner/anonymous classes check order: enclosing class must be processed first
  to correctly apply ignoring annotation (#25)
* Fix source link recognition in IDEA console when multiple signatures used: 
  changed "[Undefined reference (signature)]" to "[Undefined reference | signature]".

### 1.5.1 (2020-06-06)
* Update animalsniffer 1.16 -> 1.18 (support java > 8)

### 1.5.0 (2019-02-05)
* (breaking) Gradle 5 compatibility. Plugin now requires gradle 5.x due to gradle api changes (#12)

### 1.4.6 (2018-08-27)
* Fix build cache support for relocated project (use relative paths for cache keys) (#10). 
  Affects CI builds caching when project checked out into different directories  

### 1.4.5 (2018-07-22)
* Fix maven central and jcenter artifacts (missed dependency)

### 1.4.4 (2018-07-13)
* Fix ant task errors propagation (errors was silently hidden)
* Fix support for classes with lambdas (retrolambda plugin compatibility) (#7)

### 1.4.3 (2017-12-06)
* Fix warnings recognition for parallel builds (#3)

### 1.4.2 (2017-08-24)
* Update animalsniffer 1.15 -> 1.16
* Remove error duplicates after method return type detection (introduced in 1.16)
  For example, code line `Paths.get ( " / tmp " );` produce 2 errors: 
  unknown return type (java.nio.file.Path), unknown method (java.nio.file.Path java.nio.file.Paths.get(String, String[]))
  (note, in 1.15 only second error was shown).
  To avoid such duplicates, plugin will remove first error (check that current error starts with previous error). 

### 1.4.1 (2017-07-30)
* (breaking) Fix Gradle 4 compatibility for build signature task (due to new gradle outputs resolution restriction): 
    - Now task use output directory instead of output files, so `task.outputs.files` can't be used (will return just output directory) 
    Use new `task.outputFiles` method instead (it will also grant dependency on signature task, the same way as outputs do).
    - Separate output directory used for each task to avoid collisions: `build/animalsniffer/$taskName`
    For project signature (configured with animalsnifferSignature configuration), output directory would be
    `build/animalsniffer/signature/`. For cache tasks, animalsniffer prefix cut off from task name: 
    `build/animalsniffer/cacheMain/`.

### 1.4.0 (2017-07-18)
* Add build signature task and animalsnifferSignature configuration closure 
* (breaking) Plugin must be applied after java (groovy) plugin, otherwise it will do nothing
* Add signature name to error message when multiple signatures used for check (for better context identification) 
* Add caching mode (for check task): when enabled, extra task added for each source set to compose all provided signatures and jars 
into new project-specific signature.
    - Speed up subsequent animalsniffer check runs for large classpaths (because of no need to re-read all jars all the 
     time and ability to use smaller signature for checks). Extremely helpful for gradle plugin projects.
    - Merge multiple signatures into one (rare case, when 3rd party libraries provide signatures and they must be merged with jdk signature)
* Add cache configuration options `animalsniffer.cache` closure:
    - enabled - to enable cache tasks usage with check tasks (for advanced caching)
    - exclude - to exclude not used packages from generated signature and speed up check task 
    (by default, 'sun.*' and repackaged gradle dependencies packages are excluded)
    - mergeSignatures - to enable signatures merge (no merge, by default because case is rare)
* Add `animalsniffer` configuration options (to simplify small signatures usage):
    - excludeJars - to be able to exclude jars from check classpath (because library signatures should be used instead)
    - signatures - to be able to directly specify signatures for check (e.g. from custom location or output from other task)
* Add SignatureInfoTask to analyze signature (to be able to reduce signature size and speed-up check)

### 1.3.0 (2017-03-20)
* Prevent other tasks output interception (#3)
* Change console reporting format (to match gradle-quality-plugin format):
    - Each error split to source and code lines followed by empty line
    - IDE will be able to show link for class reference (clickable) when line number available
* Add ignored classes configuration: extra allowed classes not present in signature

### 1.2.0 (2016-08-27)
* Gradle 3.0 compatibility

### 1.1.0 (2016-06-15)
* Gradle 2.14 compatibility
* BREAKING: incompatible with old gradle versions (requires gradle >= 2.14). 
  When used with older version raise compatibility error to avoid confusion.

### 1.0.1 (2016-03-30)
* Update animalsniffer 1.14 -> 1.15 (fixes [NPE bug](https://github.com/mojohaus/animal-sniffer/issues/8))

### 1.0.0 (2015-12-20)
* Initial release