package invalid;

public class Dep2 {

    public void someth() {
        // method added in 1.7
        Boolean.compare(true, true);
    }
}