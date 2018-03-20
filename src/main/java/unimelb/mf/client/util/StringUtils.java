package unimelb.mf.client.util;

public class StringUtils {

    public static String trimPrefix(String str, String prefix, boolean repeat) {
        String r = str;
        if (repeat) {
            while (r.startsWith(prefix)) {
                r = r.substring(prefix.length());
            }
        } else {
            if (r.startsWith(prefix)) {
                r = r.substring(prefix.length());
            }
        }
        return r;
    }

    public static String trimSuffix(String str, String suffix, boolean repeat) {
        String r = str;
        if (repeat) {
            while (r.endsWith(suffix)) {
                r = r.substring(0, r.length() - suffix.length());
            }
        } else {
            if (r.endsWith(suffix)) {
                r = r.substring(0, r.length() - suffix.length());
            }
        }
        return r;
    }

    public static String stringOf(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(stringOf('1', 10));
    }
}
