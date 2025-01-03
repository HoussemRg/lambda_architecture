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
        JavaSparkContext sc = new JavaSparkContext(conf);

        SparkSession sparkSession = SparkSession.builder().config(conf).getOrCreate();

        String saveFile = prop.getProperty("tn.enit.tp4.hdfs") + "airport-data";

        // Run batch processing
        Map<String, List<AverageData>> averageDataMap = ProcessorUtils.runBatch(sparkSession, saveFile);

        // Save avg_altitude to Cassandra
        JavaRDD<AverageData> avgAltitudeRdd = sc.parallelize(averageDataMap.get("avgAltitude"), 1);
        ProcessorUtils.saveAvgToCassandra(avgAltitudeRdd, "avg_altitude_per_country");

        // Save avg_longitude to Cassandra
        JavaRDD<AverageData> avgLongitudeRdd = sc.parallelize(averageDataMap.get("avgLongitude"), 1);
        ProcessorUtils.saveAvgToCassandra(avgLongitudeRdd, "avg_longitude_per_country");

        System.out.println("Batch processing completed and data saved to Cassandra.");

        sparkSession.close();
        sparkSession.stop();
    }
}