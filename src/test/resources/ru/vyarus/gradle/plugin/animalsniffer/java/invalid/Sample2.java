package invalid;

import org.junit.Assert;

import java.nio.file.FileSystems;

public class Sample2 {

    public static void main(String[] args) {
        // added in 1.7
        FileSystems.getDefault().getFileStores();
        Assert.assertTrue(true);
    }
}