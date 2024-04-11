package com.bielcik.traveller.controller;

import com.bielcik.traveller.service.TravellerService;
import com.bielcik.traveller.service.dto.DocumentSearchDTO;
import com.bielcik.traveller.service.dto.TravellerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/api/travellers")
public class TravellerController {
    @Autowired
    private TravellerService travellerService;


    @GetMapping("/email/{email}")
    public TravellerDTO getTravellerByEmail(@PathVariable String email) {
        log.info("Get travellerByEmail: {}", email);
        return travellerService.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/mobile-phone/{mobilePhone}")
    public TravellerDTO getTravellerByMobilePhone(@PathVariable String mobilePhone) {
        log.info("Get travellerByMobilePhone: {}", mobilePhone);
        return travellerService.findByTelephoneNumber(mobilePhone).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/document")
    public TravellerDTO getTravellerByDocument(DocumentSearchDTO documentSearchDTO) {
        log.info("Get travellerByDocument: {}", documentSearchDTO);
        return travellerService.findByDocument(documentSearchDTO).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public TravellerDTO createTraveller(@RequestBody TravellerDTO travellerDTO) {
        log.info("Create traveller: {}", travellerDTO);
        if (travellerDTO.getTravellerId() != null) {
            throw new IllegalArgumentException("A new traveller cannot already have an ID");
        }
        return travellerService.save(travellerDTO);
    }

    @PutMapping
    public TravellerDTO updateTraveller(@RequestBody TravellerDTO travellerDTO) throws MissingRequestValueException {
        log.info("Update traveller: {}", travellerDTO);
        if (travellerDTO.getTravellerId() == null) {
            throw new MissingRequestValueException("Missing travellerId");
        }
        return travellerService.update(travellerDTO);
    }

    @DeleteMapping
    public void deleteTraveller(@RequestParam long travellerId) {
        log.info("Deleting traveller {}", travellerId);
        travellerService.markAsDeleted(travellerId);
    }
}
