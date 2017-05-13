* Add build signature task, created with animalsnifferSignature configuration closure 
* (breaking) Plugin must be applied after java (groovy) plugin, otherwise it will do nothing
* Extra task added for each source set to compose all provided signatures and jars into new project-specific signature.
    - Fixes multiple signatures case: when two or more signature files provided, they are merged into one (earlier, check was performed 
    for each signature separately)
    - Should speed up subsequent animalsniffer runs for large classpaths (because of no need to re-read all jars all the time)
    - Downside: first run will be a bit slower then before because of additional task
    - Resources task usage could be disabled with `useResourcesTask = false' configuration option (work exactly as before)
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