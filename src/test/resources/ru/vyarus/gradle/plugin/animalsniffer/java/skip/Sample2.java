package invalid;

import java.nio.file.Paths;

public class Sample2 {

    public static void main(String[] args) {
        // class added in 1.7
        Paths.get("/tmp");
    }
}