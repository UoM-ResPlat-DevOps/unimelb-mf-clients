package unimelb.mf.client.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

    public static final char SLASH_CHAR = '/';
    public static final char BACKSLASH_CHAR = '\\';
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";

    public static String join(char separator, String... paths) {
        if (paths != null && paths.length > 0) {
            StringBuilder sb = new StringBuilder();
            int n = 0;
            for (String path : paths) {
                if (path != null) {
                    path = path.trim().replace(SLASH_CHAR, separator).replace(BACKSLASH_CHAR, separator);
                    if (!path.isEmpty()) {
                        if (n == 0) {
                            path = trimRight(separator, path);
                        } else {
                            path = trim(separator, path);
                            sb.append(separator);
                        }
                        sb.append(path);
                        n++;
                    }
                }
            }
            if (n > 0) {
                String s = String.valueOf(separator);
                return sb.toString().replaceAll(s + "{2,}", s);
            }
        }
        return null;
    }

    public static String joinSystemDependent(String... paths) {
        return join(File.separatorChar, paths);
    }

    public static String joinSystemIndependent(String... paths) {
        return join(SLASH_CHAR, paths);
    }

    public static String trim(char c, String path) {
        return trimLeft(c, trimRight(c, path));
    }

    public static String trimLeft(char c, String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        path = path.trim();
        String s = String.valueOf(c);
        while (path.startsWith(s) && !path.equals(s)) {
            path = path.substring(s.length());
        }
        return path;
    }

    public static String trimRight(char c, String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        path = path.trim();
        String s = String.valueOf(c);
        while (path.endsWith(s) && !path.equals(s)) {
            path = path.substring(0, path.length() - s.length());
        }
        return path;
    }

    public static String toSystemDependent(String path) {
        if (path == null) {
            return path;
        }
        path = path.trim();
        if (path.isEmpty()) {
            return path;
        }
        path = path.trim().replace(SLASH_CHAR, File.separatorChar).replace(BACKSLASH_CHAR, File.separatorChar)
                .replaceAll(File.separator + "{2,}", File.separator);
        return trimRight(File.separatorChar, path);
    }

    public static String toSystemIndependent(String path) {
        if (path == null) {
            return path;
        }
        path = path.trim();
        if (path.isEmpty()) {
            return path;
        }
        path = path.trim().replace(SLASH_CHAR, SLASH_CHAR).replace(BACKSLASH_CHAR, SLASH_CHAR)
                .replaceAll(SLASH + "{2,}", SLASH);
        return trimRight(SLASH_CHAR, path);
    }

    public static Path getPath(String... paths) {
        return Paths.get(joinSystemDependent(paths));
    }

    public static File getFile(String... paths) {
        return getPath(paths).toFile();
    }

    public static String getRelativePathSI(Path parent, Path descendant) {
        // return getRelativePathSI(parent.toAbsolutePath().toString(),
        // descendant.toAbsolutePath().toString());
        return toSystemIndependent(parent.toAbsolutePath().relativize(descendant.toAbsolutePath()).toString());
    }

    public static String getRelativePathSI(String parent, String descendant) {
        parent = toSystemIndependent(parent);
        descendant = toSystemIndependent(descendant);
        if (descendant.startsWith(parent + SLASH)) {
            String relativePath = StringUtils.trimPrefix(descendant, parent, false);
            return trimLeft(SLASH_CHAR, relativePath);
        }
        return null;
    }

    public static String getRelativePathSD(Path parent, Path descendant) {
        return toSystemDependent(parent.toAbsolutePath().relativize(descendant.toAbsolutePath()).toString());
    }

    public static String getRelativePathSD(String parent, String descendant) {
        parent = toSystemDependent(parent);
        descendant = toSystemDependent(descendant);
        if (descendant.startsWith(parent + File.separator)) {
            String relativePath = StringUtils.trimPrefix(descendant, parent, false);
            return trimLeft(File.separatorChar, relativePath);
        }
        return null;
    }

    public static boolean isOrIsDescendant(Path descendant, Path parent) {
        return isOrIsDescendant(descendant == null ? null : descendant.toAbsolutePath().toString(),
                parent == null ? null : parent.toAbsolutePath().toString());
    }

    public static boolean isOrIsDescendant(String descendant, String parent) {
        String sa = descendant == null ? null : toSystemIndependent(descendant);
        String sb = parent == null ? null : toSystemIndependent(parent);
        if (sa != null && sb != null) {
            return sa.equals(sb) || sa.startsWith(sb + SLASH);
        }
        return false;
    }

    public static boolean isDescendant(Path child, Path parent) {
        return isDescendant(child == null ? null : child.toAbsolutePath().toString(),
                parent == null ? null : parent.toAbsolutePath().toString());
    }

    public static boolean isDescendant(String descendant, String parent) {
        String sa = descendant == null ? null : toSystemIndependent(descendant);
        String sb = parent == null ? null : toSystemIndependent(parent);
        if (sa != null && sb != null) {
            return sa.startsWith(sb + SLASH);
        }
        return false;
    }

    public static String getFileExtension(String path) {
        if (path != null) {
            String p = removeFileExtension(path);
            if (p != null && p.length() < path.length()) {
                return path.substring(p.length() + 1);
            }
        }
        return null;
    }

    public static String removeFileExtension(String path) {
        if (path != null) {
            if (path.matches(".+\\.\\w+$")) {
                return path.replaceAll("\\.\\w+$", "");
            }
        }
        return path;
    }

    public static String getLastComponent(String path) {
        if (path == null) {
            return null;
        }
        String p = toSystemIndependent(path);
        if (SLASH.equals(p)) {
            return p;
        }
        int idx = p.lastIndexOf(SLASH_CHAR);
        if (idx == -1) {
            return p;
        }
        return p.substring(idx + 1);
    }

    public static String getParentPath(String path) {
        if (path == null || path.trim().isEmpty() || path.length() == 1) {
            return null;
        }

        char separator = SLASH_CHAR;
        int idx = -1;
        if ((idx = path.lastIndexOf(SLASH_CHAR)) != -1) {
            separator = SLASH_CHAR;
        } else if ((idx = path.lastIndexOf(BACKSLASH_CHAR)) != -1) {
            separator = BACKSLASH_CHAR;
        } else {
            return null;
        }
        path = trimRight(separator, path.trim());
        idx = path.lastIndexOf(separator);
        if (idx != -1) {
            if (path.startsWith(String.valueOf(separator)) && path.indexOf(separator) == idx) {
                if (path.equals(String.valueOf(separator))) {
                    return null;
                } else {
                    return path.substring(0, idx + 1);
                }
            }
            return path.substring(0, idx);
        }
        return null;
    }

    public static String getFileName(String path) {

        char separator = SLASH_CHAR;
        int idx = -1;
        if ((idx = path.lastIndexOf(SLASH_CHAR)) != -1) {
            separator = SLASH_CHAR;
        } else if ((idx = path.lastIndexOf(BACKSLASH_CHAR)) != -1) {
            separator = BACKSLASH_CHAR;
        } else {
            return path;
        }
        path = trimRight(separator, path.trim());
        idx = path.lastIndexOf(separator);
        if (idx != -1) {
            if (path.startsWith(String.valueOf(separator)) && path.indexOf(separator) == idx) {
                if (path.equals(String.valueOf(separator))) {
                    return null;
                } else {
                    return path.substring(0, idx + 1);
                }
            }
            return path.substring(0, idx);
        }
        return null;

    }

    public static void main(String[] args) {
        // System.out.println(joinSystemIndependent("ab/c", "\\d\\\\e\\",
        // "eee//ddd"));
        // System.out.println(joinSystemDependent("ab/c", "\\d\\\\e\\",
        // "eee//ddd"));
        // System.out.println(getParentPath("\\a\\b\\c"));
        // System.out.println(getParentPath("/a/b/c"));
        // System.out.println(getParentPath("/a"));
        // System.out.println(getParentPath("/"));
        // System.out.println(getParentPath("\\a"));
        // System.out.println(getParentPath("\\"));
        // System.exit(0);
    }
}
