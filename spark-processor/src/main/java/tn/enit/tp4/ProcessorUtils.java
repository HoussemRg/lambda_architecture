package tn.enit.tp4;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.DStreamJavaFunctions;
import org.apache.spark.streaming.api.java.JavaDStream;
import tn.enit.tp4.AirportData;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;
import static com.datastax.spark.connector.japi.CassandraStreamingJavaUtil.javaFunctions;

public class ProcessorUtils {

    public static SparkConf getSparkConf(Properties prop) {
        var sparkConf = new SparkConf()
                .setAppName(prop.getProperty("tn.enit.tp4.spark.app.name"))
                .setMaster(prop.getProperty("tn.enit.tp4.spark.master"))
                .set("spark.cassandra.connection.host", prop.getProperty("tn.enit.tp4.cassandra.host"))
                .set("spark.cassandra.connection.port", prop.getProperty("tn.enit.tp4.cassandra.port"))
                .set("spark.cassandra.auth.username", prop.getProperty("tn.enit.tp4.cassandra.username"))
                .set("spark.cassandra.auth.password", prop.getProperty("tn.enit.tp4.cassandra.password"))
                .set("spark.cassandra.connection.keep_alive_ms", prop.getProperty("tn.enit.tp4.cassandra.keep_alive"));

        if ("local".equals(prop.getProperty("tn.enit.tp4.env"))) {
            sparkConf.set("spark.driver.bindAddress", "127.0.0.1");
        }
        return sparkConf;
    }

    public static void saveToCassandra(JavaDStream<AirportData> dataStream) {
        System.out.println("Saving to Cassandra...");

        // Define column name mappings between Cassandra table and AirportData fields
        HashMap<String, String> columnNameMappings = new HashMap<>();
        columnNameMappings.put("airportId", "airportid");
        columnNameMappings.put("name", "name");
        columnNameMappings.put("city", "city");
        columnNameMappings.put("country", "country");
        columnNameMappings.put("iata", "iata");
        columnNameMappings.put("icao", "icao");
        columnNameMappings.put("latitude", "latitude");
        columnNameMappings.put("longitude", "longitude");
        columnNameMappings.put("altitude", "altitude");
        columnNameMappings.put("timezone", "timezone");
        columnNameMappings.put("dst", "dst");
        columnNameMappings.put("databaseTimezone", "databasetimezone");
        columnNameMappings.put("type", "type");
        columnNameMappings.put("source", "source");
        columnNameMappings.put("created_at", "created_at");

        // Save the data stream to Cassandra using the mappings
        DStreamJavaFunctions<AirportData> cassandraWriter = javaFunctions(dataStream);

        cassandraWriter.writerBuilder(
                "airportkeyspace",          // Keyspace name
                "airportdata",              // Table name
                mapToRow(AirportData.class, columnNameMappings)
        ).saveToCassandra();

        System.out.println("Data successfully saved to Cassandra!");
    }

    public static void saveToHDFS(JavaDStream<AirportData> dataStream, String saveFile, SparkSession spark) {
        System.out.println("Saving to HDFS...");
        dataStream.foreachRDD(rdd -> {
            if (rdd.isEmpty()) return;
            Dataset<Row> dataFrame = spark.createDataFrame(rdd, AirportData.class);
            dataFrame.write().mode(SaveMode.Append).parquet(saveFile);
        });
    }

    public static AirportData transformData(Row row) {
        System.out.println(row);
        return new AirportData(
                row.getAs("airportId"),          // Integer
                row.getAs("name"),               // String
                row.getAs("city"),               // String
                row.getAs("country"),            // String
                row.getAs("iata"),               // String
                row.getAs("icao"),               // String
                row.getAs("latitude"),           // Double
                row.getAs("longitude"),          // Double
                row.getAs("altitude"),           // Integer
                row.getAs("timezone"),           // Integer
                row.getAs("dst"),                // String
                row.getAs("databaseTimezone"),   // String
                row.getAs("type"),               // String
                row.getAs("source")  ,
                row.getAs("created_at")// String
        );
    }

    public static Map<String, List<AverageData>> runBatch(SparkSession sparkSession, String saveFile) {
        System.out.println("Running Batch Processing...");

        // Define schema
        StructType schema = new StructType()
                .add("airportId", DataTypes.IntegerType)
                .add("name", DataTypes.StringType)
                .add("city", DataTypes.StringType)
                .add("country", DataTypes.StringType)
                .add("iata", DataTypes.StringType)
                .add("icao", DataTypes.StringType)
                .add("latitude", DataTypes.DoubleType)
                .add("longitude", DataTypes.DoubleType)
                .add("altitude", DataTypes.IntegerType)
                .add("timezone", DataTypes.IntegerType)
                .add("dst", DataTypes.StringType)
                .add("databaseTimezone", DataTypes.StringType)
                .add("type", DataTypes.StringType)
                .add("source", DataTypes.StringType)
                .add("created_at", DataTypes.TimestampType);


        // Read data with schema enforcement
        Dataset<Row> dataFrame = sparkSession.read().schema(schema).parquet(saveFile);

        // Convert to JavaRDD<AirportData>
        JavaRDD<AirportData> rdd = dataFrame.javaRDD().map(ProcessorUtils::transformData);

        // Group data by country
        Map<String, List<AirportData>> groupedByCountry = rdd.collect()
                .stream()
                .collect(Collectors.groupingBy(AirportData::getCountry));

        // Calculate averages
        List<AverageData> avgAltitude = calculateAverages(groupedByCountry, true);
        List<AverageData> avgLongitude = calculateAverages(groupedByCountry, false);

        Map<String, List<AverageData>> result = new HashMap<>();
        result.put("avgAltitude", avgAltitude);
        result.put("avgLongitude", avgLongitude);

        return result;
    }

    private static List<AverageData> calculateAverages(Map<String, List<AirportData>> groupedByCountry, boolean isAltitude) {
        List<AverageData> averages = new ArrayList<>();

        groupedByCountry.forEach((country, airports) -> {
            double total = 0;
            int count = airports.size();

            for (AirportData airport : airports) {
                total += isAltitude ? airport.getAltitude() : airport.getLongitude();
            }

            double average = total / count;
            averages.add(new AverageData(country, average,  new Timestamp(System.currentTimeMillis())));
        });

        return averages;
    }

    public static void saveAvgToCassandra(List<AverageData> data, String tableName, JavaSparkContext sc) {
        System.out.println("Saving averages to Cassandra table: " + tableName);

        // Convert List<AverageData> to JavaRDD<AverageData>
        JavaRDD<AverageData> rdd = sc.parallelize(data);

        // Use CassandraJavaUtil to save data to Cassandra
        CassandraJavaUtil.javaFunctions(rdd)
                .writerBuilder("airportkeyspace", tableName, mapToRow(AverageData.class))
                .saveToCassandra();

        System.out.println("Data successfully saved to Cassandra table: " + tableName);
    }

}
