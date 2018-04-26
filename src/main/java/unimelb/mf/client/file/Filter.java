package unimelb.mf.client.file;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public interface Filter {

    boolean acceptFile(Path file, BasicFileAttributes attrs);

    boolean acceptDirectory(Path dir, BasicFileAttributes attrs);

}
