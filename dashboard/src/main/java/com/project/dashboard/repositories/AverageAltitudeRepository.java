package com.project.dashboard.repositories;

import com.project.dashboard.entity.AverageAltitudeAirportData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AverageAltitudeRepository extends CassandraRepository<AverageAltitudeAirportData,String> {

    @Query("SELECT * FROM airportkeyspace.avg_altitude_per_country")
    Iterable<AverageAltitudeAirportData> find();
}
