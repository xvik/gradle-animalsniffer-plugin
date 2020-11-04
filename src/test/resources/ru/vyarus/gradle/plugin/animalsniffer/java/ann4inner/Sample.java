package ann;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

@IgnoreJRERequirement
public class Sample {

    public static void main(String[] args) {
        // method added in 1.7
        Boolean.compare(true, true);
    }

    public String someth() throws Exception {
        // this will compile as separate class file and animalsniffer check order would be important
        // (enclosed class must be checked first to apply annotataion)
        // https://github.com/xvik/gradle-animalsniffer-plugin/issues/25
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                // class added in 1.7
                Paths.get("/tmp");
                return "ok";
            }
        }.call();
    }
}