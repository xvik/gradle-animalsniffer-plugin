package sample;

import org.slf4j.LoggerFactory;

public class Sample {

    private String sample;

    public Sample(String sample) {
        this.sample = sample;
    }

    public static void main(String[] args) {
        LoggerFactory.getLogger(Sample.class);
    }
}