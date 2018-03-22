package unimelb.mf.client.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static void createParentDirectories(File file) throws IOException {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            Files.createDirectories(dir.toPath());
        }
    }

    public static void createParentDirectories(Path file) throws IOException {
        Path dir = file.getParent();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

}
