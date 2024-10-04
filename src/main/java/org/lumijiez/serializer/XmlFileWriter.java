package org.lumijiez.serializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class XmlFileWriter {
    public static void saveXmlToFile(Object objectToSerialize, String filePath) throws IllegalAccessException {
        String xmlOutput = Lumi.toXml(objectToSerialize);

        File file = new File(filePath);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(xmlOutput);
            System.out.println("XML saved to " + filePath);
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

