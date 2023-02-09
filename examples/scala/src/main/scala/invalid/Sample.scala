package invalid

import org.slf4j.LoggerFactory

import java.nio.file.Paths
import java.lang.Boolean

object Sample {

    def main(args: Array[String]) = {
        // method added in 1.7
        Boolean.compare(true, true)
    }

    def someth() {
        // class added in 1.7
        Paths.get("/tmp")
        LoggerFactory.getLogger("sample logger")
    }
}