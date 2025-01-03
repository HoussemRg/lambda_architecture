package tn.enit.tp4.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileReader {
    private static Properties prop = new Properties();

    public static Properties readPropertyFile() throws Exception {
        if (prop.isEmpty()) {
            InputStream input = PropertyFileReader.class.getClassLoader().getResourceAsStream("kafka-producer.properties");
            try {
                if (input != null) {
                    prop.load(input);
                } else {
                    throw new IOException("Property file 'kafka-producer.properties' not found.");
                }
            } catch (IOException ex) {
                System.out.println("Error loading properties: " + ex);
                throw ex;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        return prop;
    }
}