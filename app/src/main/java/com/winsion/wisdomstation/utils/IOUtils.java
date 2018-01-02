package com.winsion.wisdomstation.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by wyl on 2016/12/14.
 */

public class IOUtils {
    public static String read(File f) {
        BufferedReader reader = null;
        String content = "";
        try {
            reader = new BufferedReader(new FileReader(f));
            int len;
            char[] buffer = new char[1024];
            while ((len = reader.read(buffer)) != -1) {
                content += new String(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(new Closeable[]{reader});
        }
        return content;
    }

    public static void write(File file, String content) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(new Closeable[]{writer});
        }
    }

    public static void closeStream(Closeable[] stream) {
        if (stream != null && stream.length != 0) {
            for (Closeable closeable : stream) {
                try {
                    if (closeable != null) {
                        closeable.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
