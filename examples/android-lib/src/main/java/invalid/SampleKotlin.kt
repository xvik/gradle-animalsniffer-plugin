package invalid

import java.io.File

class SampleKotlin {
    fun someth() {
        // not available in android
        val file = File("")
        file.toPath()
    }
}