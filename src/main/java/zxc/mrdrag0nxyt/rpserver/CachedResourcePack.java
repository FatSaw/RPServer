package zxc.mrdrag0nxyt.rpserver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.Getter;

public final class CachedResourcePack {

    private static final byte[] HEX = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    @Getter
    private File file;

    @Getter
    private int length;

    @Getter
    private byte[] pack;

    @Getter
    private String hash;


    public CachedResourcePack() {
        update();
    }

    public void update() {
        long length;
        if (file == null || (length = this.file.length()) == 0L) {
            this.length = 0;
            pack = new byte[0];
            hash = "";
            return;
        }
        if (length > 262144000) {
            length = 262144000;
        }
        byte[] pack = new byte[(int) length];
        FileInputStream is = null;
        try {
            is = new FileInputStream(this.file);
            is.read(pack, 0, pack.length);
            is.close();
        } catch (IOException e1) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e2) {
                }
            }
        }
        final MessageDigest sha1hash;
        byte[] sha1 = null;
        try {
            sha1hash = MessageDigest.getInstance("SHA-1");
            sha1 = sha1hash.digest(pack);
            int i = sha1.length, j = i << 1;
            byte[] hexChars = new byte[j];
            while (--i > -1) {
                int v = sha1[i] & 0xFF;
                hexChars[--j] = HEX[v & 0x0F];
                hexChars[--j] = HEX[v >>> 4];
            }
            sha1 = hexChars;
        } catch (NoSuchAlgorithmException e) {
        }
        this.length = pack.length;
        this.pack = pack;
        this.hash = sha1 == null ? null : new String(sha1, StandardCharsets.US_ASCII);
    }

    public void setFile(File file) {
        this.file = file;
        update();
    }

}
