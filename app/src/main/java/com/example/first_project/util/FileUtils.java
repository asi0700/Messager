package com.example.first_project.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static File copyUriToCache(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Cannot open input stream");
        }
        File tempFile = File.createTempFile("upload_", ".tmp", context.getCacheDir());
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
        } finally {
            inputStream.close();
        }
        return tempFile;
    }
}

