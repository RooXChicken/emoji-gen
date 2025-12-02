package org.loveroo.emojigen.util;

import java.io.File;
import java.io.FileWriter;

public class FileUtil {
    
    public static void writeString(File file, String output) {
        try {
            var writer = new FileWriter(file);

            writer.write(output);
            writer.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
