package invalid

import groovy.transform.CompileStatic
import org.slf4j.LoggerFactory

import java.nio.file.Paths

@CompileStatic
class Sample {

    static void main(String[] args) {
        // method added in 1.7
        Boolean.compare(true, true)
    }

    void someth() {
        // class added in 1.7
        Paths.get("/tmp")
        LoggerFactory.getLogger(Sample.class)
    }
}