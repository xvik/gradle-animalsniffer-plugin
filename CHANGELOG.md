* Prevent potential intercepting output of other ant tasks (#3)
* Change console reporting format (to match gradle-quality-plugin format):
    - Each error split to source and code lines followed by empty line
    - IDE will be able to show link for class reference (clickable) when line number available

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