package io.vulcan.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IoUtils {
    private static final int BUFFER_SIZE = 1024 * 4;
    private static final Logger log = LoggerFactory.getLogger(IoUtils.class);

    public static byte[] toByteArray(@WillNotClose InputStream is) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] b = new byte[BUFFER_SIZE];
            int n;
            while ((n = is.read(b)) != -1) {
                output.write(b, 0, n);
            }
            return output.toByteArray();
        }
    }

    public static String toString(@WillNotClose InputStream is, Charset charset) throws IOException {
        return new String(toByteArray(is), charset);
    }

    public static String toUtf8String(@WillNotClose InputStream is) throws IOException {
        return toString(is, StandardCharsets.UTF_8);
    }

    public static long copy(@WillNotClose InputStream in, @WillNotClose OutputStream out, long readLimit) throws IOException {
        if (in instanceof FileInputStream && out instanceof FileOutputStream) {
            final FileChannel fis = ((FileInputStream) in).getChannel();
            final FileChannel fos = ((FileOutputStream) out).getChannel();
            final long size = fis.size();
            if (size >= readLimit) {
                throw new IOException("Read limit exceeded: " + readLimit);
            }
            return fis.transferTo(0, size, fos);
        }

        byte[] buf = new byte[BUFFER_SIZE];
        long count = 0;
        int n;
        while ((n = in.read(buf)) > -1) {
            out.write(buf, 0, n);
            count += n;
            if (count >= readLimit) {
                throw new IOException("Read limit exceeded: " + readLimit);
            }
        }
        return count;
    }

    public static long copy(@WillNotClose InputStream in, @WillNotClose OutputStream out) throws IOException {
        return copy(in, out, Long.MAX_VALUE);
    }

    public static void closeQuietly(@WillClose AutoCloseable is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Ignore failure in closing the Closeable", ex);
                }
            }
        }
    }
}
