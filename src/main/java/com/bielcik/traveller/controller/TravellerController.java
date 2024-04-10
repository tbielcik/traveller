package com.bielcik.traveller.controller;

import com.bielcik.traveller.service.TravellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travellers")
public class TravellerController {
    @Autowired
    private TravellerService travellerService;
    // Implement REST endpoints
}
