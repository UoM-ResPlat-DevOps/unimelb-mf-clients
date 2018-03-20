package unimelb.mf.client.util;

public class OSUtils {

    public static boolean isWindows() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("windows") >= 0;
    }

    public static boolean isLinux() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("linux") >= 0;
    }

    public static boolean isMacOSX() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("mac os x") >= 0;
    }

    public static boolean isAIX() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("aix") >= 0;
    }

    public static boolean isHPUX() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("hp-ux") >= 0;
    }

    public static boolean isIrix() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("irix") >= 0;
    }

    public static boolean isSolaris() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("solaris") >= 0;
    }

    public static boolean isSunOS() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("sunos") >= 0;
    }

    public static boolean isFreeBSD() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("freebsd") >= 0;
    }

    public static boolean isOpenBSD() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("openbsd") >= 0;
    }

    public static boolean isNetBSD() {
        return System.getProperty("os.name", "generic").toLowerCase().indexOf("netbsd") >= 0;
    }

    public static final boolean IS_WINDOWS = isWindows();

    public static final boolean IS_LINUX = isLinux();

    public static final boolean IS_MAC_OS_X = isMacOSX();

    public static final boolean IS_AIX = isAIX();

    public static final boolean IS_HP_UX = isHPUX();

    public static final boolean IS_IRIX = isIrix();

    public static final boolean IS_SOLARIS = isSolaris();

    public static final boolean IS_SUN_OS = isSunOS();

    public static final boolean IS_FREEBSD = isFreeBSD();

    public static final boolean IS_OPENBSD = isOpenBSD();

    public static final boolean IS_NETBSD = isNetBSD();

    public static final boolean IS_UNIX = IS_LINUX || IS_MAC_OS_X || IS_AIX || IS_HP_UX || IS_IRIX || IS_SOLARIS
            || IS_SUN_OS || IS_FREEBSD || IS_OPENBSD || IS_NETBSD;

    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name", "generic"));
    }

}
