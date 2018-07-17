package unimelb.mf.client.ssh.cli;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Applications {

    public static final String APP_NAME = "unimelb-mf-clients";
    public static final Path PROPERTIES_FILE = Paths.get(System.getProperty("user.home"), ".Arcitecta",
            APP_NAME + "-properties.xml");

    public static void main(String[] args) {
        System.out.println(PROPERTIES_FILE);
    }
}
