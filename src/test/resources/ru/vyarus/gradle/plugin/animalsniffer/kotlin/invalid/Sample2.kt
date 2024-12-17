package invalid

import org.slf4j.LoggerFactory
import java.nio.file.FileSystems

public class Sample2 {

    fun main() {
        // added in 1.7
        FileSystems.getDefault().fileStores
        LoggerFactory.getLogger(Sample2::class.java)
    }
}