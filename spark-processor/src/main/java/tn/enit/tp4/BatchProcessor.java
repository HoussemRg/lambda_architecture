package tn.enit.tp4;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import tn.enit.tp4.util.PropertyFileReader;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BatchProcessor {

    public static void main(String[] args) throws Exception {
        String file = "spark-processor.properties";
        Properties prop = PropertyFileReader.readPropertyFile(file);

        SparkConf conf = ProcessorUtils.getSparkConf(prop);
        SparkSession sparkSession = SparkSession.builder().config(conf).getOrCreate();

        String saveFile = prop.getProperty("tn.enit.tp4.hdfs") + "airport-data";

        // Run batch processing with schema enforcement
        Map<String, List<AverageData>> averageDataMap = ProcessorUtils.runBatch(sparkSession, saveFile);



// Save avg_altitude to Cassandra
        ProcessorUtils.saveAvgToCassandra(averageDataMap.get("avgAltitude"), "avg_altitude_per_country",
                new JavaSparkContext(sparkSession.sparkContext()));

        // Save avg_longitude to Cassandra
        ProcessorUtils.saveAvgToCassandra(averageDataMap.get("avgLongitude"), "avg_longitude_per_country",
                new JavaSparkContext(sparkSession.sparkContext()));

        System.out.println("Batch processing completed and data saved to Cassandra.");

        sparkSession.close();
    }
}