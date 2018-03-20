package unimelb.mf.client.util;

/**
 * This class follows the pattern syntax of ANT.
 * 
 * See http://ant.apache.org/manual/dirtasks.html#patterns
 * 
 */
public class PathPattern {

    public static boolean matches(String path, String pattern) {
        return path.matches(toRegEx(pattern));
    }

    public static String toRegEx(String pattern) {
        String[] parts = pattern.split("\\*\\*", -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("\\*", "[^\\" + PathUtils.SLASH + "]*");
            parts[i] = parts[i].replaceAll("\\?", "[^\\" + PathUtils.SLASH + "]{1}");
        }
        String regEx = String.join(".*", parts);
        regEx = regEx.replaceAll("\\.\\*\\/", "(.*/)?").replaceAll("\\/\\.\\*", "(/.*)?");
        return regEx;
    }

    public static void main(String[] args) {
        String pattern1 = "**/CVS/*";
        // expected to be true
        System.out.println(matches("CVS/Repository", pattern1));
        System.out.println(matches("org/apache/CVS/Entries", pattern1));
        System.out.println(matches("org/apache/jakarta/tools/ant/CVS/Entries", pattern1));
        // expected to be false
        System.out.println(matches("org/apache/CVS/foo/bar/Entries", pattern1));

        String pattern2 = "org/apache/jakarta/**";
        // expected to be true
        System.out.println(matches("org/apache/jakarta/tools/ant/docs/index.html", pattern2));
        System.out.println(matches("org/apache/jakarta/test.xml", pattern2));
        // expected to be false
        System.out.println(matches("org/apache/xyz.java", pattern2));

        String pattern3 = "org/apache/**/CVS/*";
        // expected to be true
        System.out.println(matches("org/apache/CVS/Entries", pattern3));
        System.out.println(matches("org/apache/jakarta/tools/ant/CVS/Entries", pattern3));
        // expected to be false
        System.out.println(matches("org/apache/CVS/foo/bar/Entries", pattern3));

        String pattern4 = "**/test/**";
        // expected to be true
        System.out.println(matches("test/1/2/3", pattern4));
        System.out.println(matches("a/test", pattern4));
        System.out.println(matches("a/test/1", pattern4));
        // expected to be false
        System.out.println(matches("a/test1", pattern4));
        System.out.println(matches("atest/1", pattern4));

        String pattern5 = "*/wilson*";
        // expected to be true
        System.out.println(matches("test/wilson", pattern5));
        System.out.println(matches("test2/wilson1", pattern5));
        System.out.println(matches("test3/wilson2", pattern5));
        System.out.println(matches("/wilson1", pattern5));
        // expected to be false
        System.out.println(matches("a/b/test/wilson", pattern5));
        System.out.println(matches("wilson", pattern5));

    }

}
