package retrolambda;

import java.util.ArrayList;
import java.util.List;

public class Sample {

    public static void main(String[] args) {
        List<String> b = new ArrayList<>();
        b.stream().forEach(System.out::println);
    }
}