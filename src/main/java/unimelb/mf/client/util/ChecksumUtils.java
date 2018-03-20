package unimelb.mf.client.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class ChecksumUtils {

    private ChecksumUtils() {
    }

    public static enum ChecksumType {
        CRC32, MD5, SHA1, SHA256
    }

    public static String get(File f, ChecksumType type) throws Throwable {
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        try {
            return get(in, type);
        } finally {
            in.close();
        }
    }

    public static String get(InputStream in, ChecksumType type) throws Throwable {
        switch (type) {
        case CRC32:
            return getCRC32(in);
        case MD5:
            return getMD5(in);
        case SHA1:
            return getSHA1(in);
        case SHA256:
            return getSHA256(in);
        default:
            break;
        }
        throw new IllegalArgumentException("Unknown checksum type: " + type);
    }

    public static long getCRC32Value(Path f) throws Throwable {
        return getCRC32Value(f.toFile());
    }

    public static long getCRC32Value(File f) throws Throwable {
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        try {
            return getCRC32Value(in);
        } finally {
            in.close();
        }
    };

    public static long getCRC32Value(InputStream in) throws Throwable {
        CheckedInputStream cin = new CheckedInputStream(
                (in instanceof BufferedInputStream) ? in : new BufferedInputStream(in), new CRC32());
        byte[] buffer = new byte[1024];
        try {
            while (cin.read(buffer) != -1) {
                // Read file in completely
            }
        } finally {
            cin.close();
            in.close();
        }
        return cin.getChecksum().getValue();
    }

    public static String getCRC32(File f) throws Throwable {
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        try {
            return getCRC32(in);
        } finally {
            in.close();
        }
    }

    public static String getCRC32(InputStream in) throws Throwable {
        CheckedInputStream cin = new CheckedInputStream(
                (in instanceof BufferedInputStream) ? in : new BufferedInputStream(in), new CRC32());
        byte[] buffer = new byte[1024];
        try {
            while (cin.read(buffer) != -1) {
                // Read file in completely
            }
        } finally {
            cin.close();
            in.close();
        }
        long value = cin.getChecksum().getValue();
        return Long.toHexString(value);
    }

    public static String getSHA1(File f) throws Throwable {
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        try {
            return getSHA1(in);
        } finally {
            in.close();
        }
    }

    public static String getSHA1(InputStream in) throws Throwable {
        return getDigest("SHA-1", in);
    }

    public static String getSHA256(File f) throws Throwable {
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        try {
            return getSHA256(in);
        } finally {
            in.close();
        }
    }

    public static String getSHA256(InputStream in) throws Throwable {
        return getDigest("SHA-256", in);
    }

    public static String getMD5(File f) throws Throwable {
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        try {
            return getMD5(in);
        } finally {
            in.close();
        }
    }

    public static String getMD5(InputStream in) throws Throwable {
        return getDigest("MD5", in);
    }

    public static byte[] getDigest(InputStream in, String algorithm) throws Throwable {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        DigestInputStream dis = new DigestInputStream(in, md);
        try {
            byte[] buffer = new byte[1024];
            while (dis.read(buffer) != -1) {
                // Read the stream fully
            }
        } finally {
            dis.close();
            in.close();
        }
        return md.digest();
    }

    public static String getDigest(String algorithm, InputStream in) throws Throwable {
        return toHexString(getDigest(in, algorithm));
    }

    public static String toHexString(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

}
