package unimelb.mf.client.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    public static void createParentDirectories(File file) throws IOException {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            Files.createDirectories(dir.toPath());
        }
    }

}
