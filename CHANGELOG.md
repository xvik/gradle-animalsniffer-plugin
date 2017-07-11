* Add build signature task, created with animalsnifferSignature configuration closure 
* (breaking) Plugin must be applied after java (groovy) plugin, otherwise it will do nothing
* Add signature name to error message when multiple signatures used for check (for better problem identification) 
* Add advanced caching mode: when enabled, extra task added for each source set to compose all provided signatures and jars into new project-specific signature.
 Useful to:
    - Speed up subsequent animalsniffer check runs for large classpaths (because of no need to re-read all jars all the 
     time and ability to use smaller signature for checks). Extremely helpful for gradle plugin projects.
    - Merge multiple signatures into one (rare case, when 3rd party libraries provide signatures and they must be merged with jdk signature)
* Add `animalsniffer` configuration options:
    - useResourcesTask - to enable resources tasks usage with check tasks (for advanced caching)
    - resourcesExclude - to exclude not used packages from generated signature (when useResourcesTask enabled) and speed up check task 
    (by default, 'sun.*' and repackaged gradle dependencies packages are excluded)
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