package com.project.dashboard.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
@Table("avg_altitude_per_country")
public class AverageAltitudeAirportData implements Serializable {
    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.PARTITIONED)
    private UUID id;
    private String country;
    private double average;
    private Timestamp timestamp;

    public AverageAltitudeAirportData(String country, double average, Timestamp timestamp) {
        this.id = UUID.randomUUID();
        this.country = country;
        this.average = average;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getCountry() { return country; }
    public double getAverage() { return average; }
    public Timestamp getTimestamp() { return timestamp; }
}
