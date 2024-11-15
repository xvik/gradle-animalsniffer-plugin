package invalid;

import java.io.File;

public class Sample {

    public static void main(String[] args) {
        // method added in 1.7
        Boolean.compare(true, true);
    }

    public void someth() {
        // not available in android
        File file = new File("");
        file.toPath();
    }
}