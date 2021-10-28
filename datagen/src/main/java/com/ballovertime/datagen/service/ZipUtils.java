package com.ballovertime.datagen.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class ZipUtils {

    public static byte[] compress(byte[] bytes) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length)) {
            try (GZIPOutputStream gzipOS = new GZIPOutputStream(bos)) {
                gzipOS.write(bytes);
            }
            return bos.toByteArray();
        }
    }
}
