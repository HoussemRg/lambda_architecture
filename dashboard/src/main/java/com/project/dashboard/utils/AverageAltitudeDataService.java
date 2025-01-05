package com.project.dashboard.utils;

import com.project.dashboard.entity.AverageAltitudeAirportData;
import com.project.dashboard.repositories.AverageAltitudeRepository;
import com.project.dashboard.repositories.AverageLongitudeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AverageAltitudeDataService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private AverageAltitudeRepository averageAltitudeRepository;

    @Autowired
    private AverageLongitudeRepository averageLongitudeRepository;

    @Scheduled(fixedRate = 30000)
    public void trigger() {
        try{
            System.out.println("triggered altitude");
            List<AverageAltitudeAirportData> averageAltitudeAirportData = new ArrayList<>();


            averageAltitudeRepository.find().forEach(a-> averageAltitudeAirportData.add(a));

            // send to ui
            this.template.convertAndSend("/topic/avg-altitude-data", averageAltitudeAirportData);
        }catch (
                Exception e
        ){
            System.err.println("Error in scheduled task: " + e.getMessage());
        }

    }


}
