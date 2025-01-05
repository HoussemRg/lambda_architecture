package tn.enit.tp4;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class AirportData implements Serializable {
    private int airportId;
    private String name;
    private String city;
    private String country;
    private String iata;
    private String icao;
    private double latitude;
    private double longitude;
    private int altitude;
    private int timezone;
    private String dst;
    private String databaseTimezone;
    private String type;
    private String source;
    private Timestamp created_at ;


    public AirportData() {}

    public AirportData(int airportId, String name, String city, String country, String iata, String icao,
                       double latitude, double longitude, int altitude, int timezone,
                       String dst, String databaseTimezone, String type, String source,Timestamp created_at ) {
        this.airportId = airportId;
        this.name = name;
        this.city = city;
        this.country = country;
        this.iata = iata;
        this.icao = icao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timezone = timezone;
        this.dst = dst;
        this.databaseTimezone = databaseTimezone;
        this.type = type;
        this.source = source;
        this.created_at  = created_at ;
    }

    // Getters and Setters
    public int getAirportId() { return airportId; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getIata() { return iata; }
    public String getIcao() { return icao; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getAltitude() { return altitude; }
    public int getTimezone() { return timezone; }
    public String getDst() { return dst; }
    public String getDatabaseTimezone() { return databaseTimezone; }
    public String getType() { return type; }
    public String getSource() { return source; }

    public Timestamp getCreated_at() {
        return created_at ;
    }

    public void setCreated_at(Timestamp created_at ) {
        this.created_at  = created_at ;
    }
}