package valid;

import org.slf4j.LoggerFactory;

public class Sample {

    private String sample;

    public Sample(String sample) {
        this.sample = sample;
    }

    public static void main(String[] args) {
        LoggerFactory.getLogger(Sample.class);
    }

    // new method to change signature and avoid gradle smart detectors
    public String newMethod() {
        return "null";
    }
}