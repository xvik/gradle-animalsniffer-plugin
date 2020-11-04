package ann;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import java.nio.file.Paths;

@IgnoreJRERequirement
public class Sample2 {

    public static void main(String[] args) {
        // method added in 1.7
        Boolean.compare(true, true);
    }

    public class Inner {
        public void someth() {
            // class added in 1.7
        }
    }
}