package zone.hwj.vulcan.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class IoUtilsTest {

    private final static String RESOURCE_STR = "write some content.\n这行是中文。\n\n！@#¥%¥\n!@#$%^";
    private final static byte[] RESOURCE_BYTES = RESOURCE_STR.getBytes(StandardCharsets.UTF_8);

    private final static String BYTES_STR = "write some content.\n这行是中文。\n\n！@#¥%¥\n!@#$%^__bytes";
    private final static byte[] BYTES_BYTES = BYTES_STR.getBytes(StandardCharsets.UTF_8);

    private final static String FILE_STR = "write some content.\n这行是中文。\n\n！@#¥%¥\n!@#$%^__file";
    private final static byte[] FILE_BYTES = FILE_STR.getBytes(StandardCharsets.UTF_8);


    private static final File tempFile = new File("test_file");
    private static final File tempOutFile = new File("test_file_out");

    private static Supplier<InputStream> resourceInputStream;
    private static Supplier<InputStream> bytesInputStream;
    private static Supplier<InputStream> fileInputStream;
    private static Supplier<InputStream> outFileInputStream;

    @BeforeAll
    static void beforeAll() throws IOException {

        if(!tempFile.createNewFile()) {
            throw new IOException("tempFile create error");
        }

        if(!tempOutFile.createNewFile()) {
            throw new IOException("tempOutFile create error");
        }

        resourceInputStream = () -> IoUtilsTest.class.getClassLoader().getResourceAsStream("io_test.txt");
        bytesInputStream = () -> new ByteArrayInputStream(BYTES_BYTES);
        fileInputStream = () -> {
            try {
                return new FileInputStream(tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        };
        outFileInputStream = () -> {
            try {
                return new FileInputStream(tempOutFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        };

        try (FileOutputStream fo = new FileOutputStream(tempFile)) {
            fo.write(FILE_BYTES);
        }

        System.out.println("tempFile: " + tempFile);
        System.out.println("tempOutFile: " + tempOutFile);
    }

    @AfterAll
    static void afterAll() throws IOException {

        if(!tempFile.delete()) {
            throw new IOException("tempFile close error");
        }
        if(!tempOutFile.delete()) {
            throw new IOException("tempOutFile close error");
        }
    }

    @Test
    void toByteArray() throws IOException {
        try(InputStream ris = resourceInputStream.get();
            InputStream bis = bytesInputStream.get();
            InputStream fis = fileInputStream.get()) {
            assertArrayEquals(RESOURCE_BYTES, IoUtils.toByteArray(ris));
            assertArrayEquals(BYTES_BYTES, IoUtils.toByteArray(bis));
            assertArrayEquals(FILE_BYTES, IoUtils.toByteArray(fis));
        }
    }

    @Test
    void testToString() throws IOException {
        try(InputStream ris = resourceInputStream.get()) {
            assertEquals(RESOURCE_STR, IoUtils.toString(ris, StandardCharsets.UTF_8));
        }
        try(InputStream ris = resourceInputStream.get()) {
            assertEquals(RESOURCE_STR, IoUtils.toUtf8String(ris));
        }

        try(InputStream bis = bytesInputStream.get()) {
            assertEquals(BYTES_STR, IoUtils.toString(bis, StandardCharsets.UTF_8));
        }
        try(InputStream bis = bytesInputStream.get()) {
            assertEquals(BYTES_STR, IoUtils.toUtf8String(bis));
        }

        try(InputStream fis = fileInputStream.get()) {
            assertEquals(FILE_STR, IoUtils.toString(fis, StandardCharsets.UTF_8));
        }
        try(InputStream fis = fileInputStream.get()) {
            assertEquals(FILE_STR, IoUtils.toUtf8String(fis));
        }
    }

    @Test
    void copy() throws IOException {
        try(ByteArrayOutputStream bytesOs = new ByteArrayOutputStream(BYTES_BYTES.length);
                ByteArrayOutputStream resOs = new ByteArrayOutputStream(RESOURCE_BYTES.length);
                InputStream bis = bytesInputStream.get();
                InputStream ris = resourceInputStream.get()) {
            assertEquals(BYTES_BYTES.length, IoUtils.copy(bis, bytesOs));
            assertEquals(RESOURCE_BYTES.length, IoUtils.copy(ris, resOs));

            assertArrayEquals(BYTES_BYTES, bytesOs.toByteArray());
            assertArrayEquals(RESOURCE_BYTES, resOs.toByteArray());
        }

        try(FileOutputStream fos = new FileOutputStream(tempOutFile);
            InputStream bis = bytesInputStream.get();
            InputStream fis = fileInputStream.get()) {
            assertEquals(BYTES_BYTES.length, IoUtils.copy(bis, fos));
            assertEquals(FILE_BYTES.length, IoUtils.copy(fis, fos));

            try(InputStream fisOut = outFileInputStream.get()){
                assertEquals(BYTES_STR + FILE_STR, IoUtils.toUtf8String(fisOut));
            }
        }
    }

    @Test
    void closeQuietly() {
        InputStream eis = new ErrorInputStream();

        assertThrows(IOException.class, eis::close);
        assertDoesNotThrow(() -> IoUtils.closeQuietly(eis));
    }

    static class ErrorInputStream extends InputStream {

        @Override
        public int read() {
            return 0;
        }

        @Override
        public void close() throws IOException {
            throw new IOException("test exception");
        }
    }
}