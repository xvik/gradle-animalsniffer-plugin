package invalid;

import javax.naming.InitialContext;

public class Sample {

    public static void main(String[] args) {
        // method added in 1.7
        Boolean.compare(true, true);
    }

    public void someth() throws Exception {
        // not available in android
        InitialContext.doLookup("some");
    }
}