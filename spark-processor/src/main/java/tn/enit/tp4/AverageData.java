package tn.enit.tp4;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class AverageData implements Serializable {
    private UUID id;
    private String country;
    private double average;
    private Date timestamp;

    public AverageData(String country, double average, Date timestamp) {
        this.id = UUID.randomUUID();
        this.country = country;
        this.average = average;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getCountry() { return country; }
    public double getAverage() { return average; }
    public Date getTimestamp() { return timestamp; }
}