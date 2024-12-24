package invalid

import org.slf4j.LoggerFactory

import java.nio.file.Paths

public class Sample3 {

    fun main() {
        // method added in 1.7
        java.lang.Boolean.compare(true, true)
    }

    fun someth() {
        // class added in 1.7
        Paths.get("/tmp")
        LoggerFactory.getLogger(Sample::class.java)
    }
}