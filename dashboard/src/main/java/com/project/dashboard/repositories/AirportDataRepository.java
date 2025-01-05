package com.project.dashboard.repositories;

import com.project.dashboard.entity.Airport;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AirportDataRepository extends CassandraRepository<Airport,String> {
    @Query("SELECT * FROM airportkeyspace.airportdata WHERE created_at > ?0 ALLOW FILTERING ")
    Iterable<Airport> findAirports(Date date);

}
