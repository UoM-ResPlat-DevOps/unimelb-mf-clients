package unimelb.mf.client.util;

public class ObjectUtils {

    public static boolean equals(Object a, Object b) {
        return (a == null && b == null) || (a != null && b != null && a.equals(b));
    }
}
