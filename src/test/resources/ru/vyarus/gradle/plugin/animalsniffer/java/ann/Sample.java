package ann;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import java.nio.file.Paths;

public class Sample {

    @IgnoreJRERequirement
    public static void main(String[] args) {
        // method added in 1.7
        Boolean.compare(true, true);
    }

    public void someth() {
        // class added in 1.7
        Paths.get("/tmp");
    }
}