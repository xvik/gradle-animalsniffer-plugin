package valid;

import org.junit.Assert;

public class Sample {

    private String sample;

    public Sample(String sample) {
        this.sample = sample;
    }

    public static void main(String[] args) {
        Assert.assertTrue(true);
    }

    // new method to change signature and avoid gradle smart detectors
    public String newMethod() {
        return "null";
    }
}