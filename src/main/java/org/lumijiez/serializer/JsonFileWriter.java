package org.lumijiez.serializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class JsonFileWriter {
    public static void saveJsonToFile(Object objectToSerialize, String filePath) throws IllegalAccessException {
        String jsonOutput = Lumi.toJson(objectToSerialize);

        File file = new File(filePath);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(jsonOutput);
            System.out.println("JSON saved to " + filePath);
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }
            }
        }
    }
}