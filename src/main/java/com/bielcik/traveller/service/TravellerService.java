package com.bielcik.traveller.service;

import com.bielcik.traveller.domain.Traveller;
import com.bielcik.traveller.repository.TravellerRepository;
import com.bielcik.traveller.service.dto.DocumentSearchDTO;
import com.bielcik.traveller.service.dto.TravellerDTO;
import com.bielcik.traveller.service.dto.TravellerDocumentDTO;
import com.bielcik.traveller.service.mapper.TravellerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TravellerService {

    private final TravellerRepository travellerRepository;
    private final TravellerMapper travellerMapper;

    public TravellerService(TravellerRepository travellerRepository, TravellerMapper travellerMapper) {
        this.travellerRepository = travellerRepository;
        this.travellerMapper = travellerMapper;
    }

    public TravellerDTO save(TravellerDTO travellerDTO) {
        log.info("Going to save Traveller: {}", travellerDTO);
        travellerDTO.getTravellerDocuments().forEach(document -> document.setTravellerId(travellerDTO.getTravellerId()));

        setActiveDocumentIfOnlyOneIsPresent(travellerDTO);
        checkForActiveDocument(travellerDTO);

        travellerDTO.setDeleted(false);
        Traveller traveller = travellerRepository.save(travellerMapper.toEntity(travellerDTO));
        return travellerMapper.toDto(traveller);
    }

    private static void setActiveDocumentIfOnlyOneIsPresent(TravellerDTO travellerDTO) {
        if (travellerDTO.getTravellerDocuments().size() == 1) {
            travellerDTO.getTravellerDocuments().get(0).setActive(true);
        }
    }

    private static void checkForActiveDocument(TravellerDTO travellerDTO) {
        if (!travellerDTO.getTravellerDocuments().isEmpty() && travellerDTO.getTravellerDocuments().stream().noneMatch(TravellerDocumentDTO::isActive)) {
            throw new IllegalArgumentException("At least one document needs to be active");
        }
    }

    public TravellerDTO update(TravellerDTO travellerDTO) {
        log.info("Going to update Traveller: {}", travellerDTO);
        Optional<Traveller> traveller = travellerRepository.findByTravellerIdAndDeletedIsFalse(travellerDTO.getTravellerId());
        if (traveller.isEmpty()) {
            throw new RuntimeException("Invalid traveller id: " + travellerDTO.getTravellerId());
        }
        return save(travellerDTO);
    }

    public Optional<TravellerDTO> findByEmail(String email) {
        Optional<Traveller> traveller = travellerRepository.findByEmailAndDeletedIsFalse(email);
        return traveller.map(travellerMapper::toDto);
    }

    public Optional<TravellerDTO> findByTelephoneNumber(String telephoneNumber) {
        Optional<Traveller> traveller = travellerRepository.findByMobileNumberAndDeletedIsFalse(telephoneNumber);
        return traveller.map(travellerMapper::toDto);
    }

    public Optional<TravellerDTO> findByDocument(DocumentSearchDTO documentSearchDTO) {
        Optional<Traveller> traveller = travellerRepository.findByDocumentTypeAndNumberAndIssuingCountryAndActive(documentSearchDTO.getDocumentType(), documentSearchDTO.getDocumentNumber(), documentSearchDTO.getDocumentIssuingCountry());
        return traveller.map(travellerMapper::toDto);
    }

    public void markAsDeleted(Long id) {
        log.info("Going to mark as deleted Traveller by id: {}", id);
        Optional<Traveller> travellerOptional = travellerRepository.findById(id);
        if (travellerOptional.isEmpty()) {
            throw new RuntimeException("Traveller with id " + id + " not found");
        }

        Traveller traveller = travellerOptional.get();
        traveller.setDeleted(true);
        travellerRepository.save(traveller);
    }

}
