package com.bielcik.traveller.service;

import com.bielcik.traveller.domain.DocumentType;
import com.bielcik.traveller.domain.Traveller;
import com.bielcik.traveller.repository.TravellerRepository;
import com.bielcik.traveller.service.dto.TravellerDTO;
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
        Traveller traveller = travellerRepository.save(travellerMapper.toEntity(travellerDTO));
        return travellerMapper.toDto(traveller);
    }

    public Optional<TravellerDTO> findByEmail(String email) {
        Optional<Traveller> traveller = travellerRepository.findByEmailAndDeletedIsFalse(email);
        return traveller.map(travellerMapper::toDto);
    }

    public Optional<TravellerDTO> findByTelephoneNumber(String telephoneNumber) {
        Optional<Traveller> traveller = travellerRepository.findByMobileNumberAndDeletedIsFalse(telephoneNumber);
        return traveller.map(travellerMapper::toDto);
    }

    public Optional<TravellerDTO> findByDocument(String documentNumber, DocumentType documentType, String documentIssuingCountry) {
        Optional<Traveller> traveller = travellerRepository.findByDocumentTypeAndNumberAndIssuingCountryAndActive(documentType, documentNumber, documentIssuingCountry);
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
