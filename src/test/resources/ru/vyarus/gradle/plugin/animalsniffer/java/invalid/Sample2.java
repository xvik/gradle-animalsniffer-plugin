package invalid;

import java.nio.file.FileSystems;

public class Sample2 {

    public static void main(String[] args) {
        // added in 1.7
        FileSystems.getDefault().getFileStores();
    }
}