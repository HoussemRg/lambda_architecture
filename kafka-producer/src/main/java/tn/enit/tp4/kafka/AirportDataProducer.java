package tn.enit.tp4.kafka;

import kafka.producer.ProducerConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import java.util.Properties;
import java.util.*;

public class AirportDataProducer {

    private final Producer<String, AirportData> producer;

    public AirportDataProducer(final Producer<String, AirportData> producer) {
        this.producer = producer;
    }
    public static void main(String[] args) throws Exception {
        Properties properties = PropertyFileReader.readPropertyFile();
        Producer<String, AirportData> producer = new Producer<>(new ProducerConfig(properties));
        AirportDataProducer airportDataProducer = new AirportDataProducer(producer);
        airportDataProducer.generateAirportData(properties.getProperty("kafka.topic"));
    }

    private void generateAirportData(String topic) throws InterruptedException {
        Random random = new Random();
        System.out.println("Sending Airport Data...");

        while (true) {
            AirportData airport = createRandomAirportData(random);
            producer.send(new KeyedMessage<>(topic,airport));
            Thread.sleep(random.nextInt(5000 - 1000) + 1000); // Random delay between 1-5 seconds
        }
    }

    private AirportData createRandomAirportData(Random random) {


        // Liste des pays et fuseaux horaires associés
        Map<String, String> countryToTimezone = new HashMap<>();
        countryToTimezone.put("Papua New Guinea", "Pacific/Port_Moresby");
        countryToTimezone.put("Greenland", "America/Godthab");
        countryToTimezone.put("Iceland", "Atlantic/Reykjavik");
        countryToTimezone.put("Canada", "America/Toronto");
        countryToTimezone.put("USA", "America/New_York");
        countryToTimezone.put("France", "Europe/Paris");
        countryToTimezone.put("Germany", "Europe/Berlin");

        String[] countries = countryToTimezone.keySet().toArray(new String[0]);
        String[] cities = {"Goroka", "Madang", "Reykjavik", "Winnipeg", "New York", "Paris", "Berlin"};
        String[] types = {"airport", "heliport", "seaplane base"};
        String[] dstValues = {"E", "U", "N", "A"};

        int airportId = random.nextInt(10000);
        String name = "Airport " + airportId;
        String country = countries[random.nextInt(countries.length)];
        String city = cities[random.nextInt(cities.length)];
        String iata = "I" + (char) (65 + random.nextInt(26)) + (char) (65 + random.nextInt(26));
        String icao = "C" + (char) (65 + random.nextInt(26)) + (char) (65 + random.nextInt(26));
        double latitude = random.nextDouble() * 180 - 90; // [-90, 90]
        double longitude = random.nextDouble() * 360 - 180; // [-180, 180]
        int altitude = random.nextInt(15000); // Altitude in feet
        int timezone = random.nextInt(25) - 12; // Timezones [-12, +12]
        String dst = dstValues[random.nextInt(dstValues.length)];
        String databaseTimezone = countryToTimezone.get(country); // Récupération du fuseau horaire
        String type = "airport";
        String source = "OurAirports";
        return new AirportData(airportId, name, city, country, iata, icao, latitude, longitude,
                altitude, timezone, dst, databaseTimezone, type, source);
    }


}