package invalid;

import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class Sample {

    public static void main(String[] args) {
        // method added in 1.7
        Boolean.compare(true, true);
    }

    public void someth() {
        // class added in 1.7
        Paths.get("/tmp");
        LoggerFactory.getLogger(Sample.class);
    }
}