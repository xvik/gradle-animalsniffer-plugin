package invalid;

import org.junit.Assert;

import java.nio.file.Paths;

public class Sample {

    public static void main(String[] args) {
        // method added in 1.7
        Boolean.compare(true, true);
    }

    public void someth() {
        // class added in 1.7
        Paths.get("/tmp");
        Assert.assertTrue(true);
    }
}