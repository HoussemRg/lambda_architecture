package com.project.dashboard.utils;

import com.project.dashboard.entity.Airport;
import com.project.dashboard.repositories.AirportDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AirportService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private AirportDataRepository airportDataRepository;

    // Method sends data message in every 30 seconds.
    @Scheduled(fixedRate = 30000)
    public void trigger() {
        try {
            System.out.println("triggered airprot data");
            List<Airport> airports = new ArrayList<>();
            Long time = new Date().getTime();
            Date date = new Date(time - time % ( 60 * 1000));
            airportDataRepository.findAirports(date).forEach(airports::add);
            this.template.convertAndSend("/topic/airport-data", airports);
        } catch (Exception e) {
            System.err.println("Error in scheduled task: " + e.getMessage());
        }
    }



}
