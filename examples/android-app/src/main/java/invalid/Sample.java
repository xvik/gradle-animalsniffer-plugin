package invalid;

import java.io.File;

public class Sample {

    public static void main(String[] args) {
        // method added in 11
        "".repeat(5);
    }

    public void someth() {
        // not available in android
        File file = new File("");
        file.toPath();
    }
}