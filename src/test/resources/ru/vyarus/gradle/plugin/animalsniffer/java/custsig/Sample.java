package custsig;

import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

public class Sample {

    public static void main(String[] args) {
        // api present in 1.5.3 (line must not be errored)
        LoggerFactory.getLogger("goodapi");
        // method appear in 1.5.4
        MarkerFactory.getMarker("sample").hasReferences();
    }
}