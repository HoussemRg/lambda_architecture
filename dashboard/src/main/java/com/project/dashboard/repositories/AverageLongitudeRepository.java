package com.project.dashboard.repositories;

import com.project.dashboard.entity.AverageAltitudeAirportData;
import com.project.dashboard.entity.AverageLongitudeAirportData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AverageLongitudeRepository extends CassandraRepository<AverageLongitudeAirportData,String> {

    @Query("SELECT * FROM airportkeyspace.avg_longitude_per_country")
    Iterable<AverageLongitudeAirportData> find();
}
