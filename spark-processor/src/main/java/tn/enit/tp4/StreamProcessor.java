package tn.enit.tp4;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import tn.enit.tp4.AirportData;
import tn.enit.tp4.AirportDataDeserializer;
import tn.enit.tp4.util.PropertyFileReader;

import java.util.*;

public class StreamProcessor {
    public static void main(String[] args) throws Exception {
        String file = "spark-processor.properties";
        Properties prop = PropertyFileReader.readPropertyFile(file);

        SparkConf conf = ProcessorUtils.getSparkConf(prop);
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(10));
        SparkSession sparkSession = SparkSession.builder().config(conf).getOrCreate();

        streamingContext.checkpoint(prop.getProperty("tn.enit.tp4.spark.checkpoint.dir"));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, prop.getProperty("tn.enit.tp4.kafka.brokerlist"));
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AirportDataDeserializer.class);
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, prop.getProperty("tn.enit.tp4.kafka.topic"));
        kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, prop.getProperty("tn.enit.tp4.kafka.resetType"));
        kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,  Boolean.FALSE);

        Collection<String> topics = Arrays.asList(prop.getProperty("tn.enit.tp4.kafka.topic"));
        JavaInputDStream<ConsumerRecord<String, AirportData>> stream =
                KafkaUtils.createDirectStream(streamingContext, LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.Subscribe(topics, kafkaParams));

        JavaDStream<AirportData> airportDataStream = stream.map(ConsumerRecord::value);
        airportDataStream.print();

        ProcessorUtils.saveToCassandra(airportDataStream);
        String saveFile = prop.getProperty("tn.enit.tp4.hdfs") + "airport-data";
        ProcessorUtils.saveToHDFS(airportDataStream, saveFile, sparkSession);

        streamingContext.start();
        streamingContext.awaitTermination();
    }
}