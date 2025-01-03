package tn.enit.tp4.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class AirportDataEncoder implements Encoder<AirportData> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public AirportDataEncoder(VerifiableProperties verifiableProperties) {}

    public byte[] toBytes(AirportData event) {
        try {
            String msg = objectMapper.writeValueAsString(event);
            System.out.println(msg);
            return msg.getBytes();
        } catch (JsonProcessingException e) {
            System.out.println("Error in Serialization" +e.getMessage());
        }
        return null;
    }
}