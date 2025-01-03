package tn.enit.tp4;


import com.fasterxml.jackson.databind.ObjectMapper;
import tn.enit.tp4.AirportData;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class AirportDataDeserializer implements Deserializer<AirportData> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AirportData deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, AirportData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}
    @Override
    public void close() {}
}
