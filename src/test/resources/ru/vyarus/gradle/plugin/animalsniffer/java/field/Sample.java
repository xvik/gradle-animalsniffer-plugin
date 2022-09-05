package invalid;

import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Sample {

    private Path field;

    public static void main(String[] args) {
        // method added in 1.7
        Boolean.compare(true, true);
    }
}