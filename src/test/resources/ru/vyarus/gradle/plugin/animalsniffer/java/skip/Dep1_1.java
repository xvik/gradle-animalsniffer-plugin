package invalid;

import java.nio.file.Paths;

public class Dep1 {

    public static void main(String[] args) {
        // class added in 1.7
        Paths.get("/tmp");
        new invalid.Dep2().someth();
    }
}