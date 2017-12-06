* Fix output warnings recognition for parallel builds (#3)

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