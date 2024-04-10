package com.bielcik.traveller.repository;

import com.bielcik.traveller.domain.DocumentType;
import com.bielcik.traveller.domain.Traveller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TravellerRepository extends JpaRepository<Traveller, Long> {
    Optional<Traveller> findByEmailAndDeletedIsFalse(String email);
    Optional<Traveller> findByMobileNumberAndDeletedIsFalse(String mobileNumber);
    Optional<Traveller> findByTravellerIdAndDeletedIsFalse(long id);

    @Query("SELECT t FROM Traveller t JOIN FETCH t.travellerDocuments d WHERE t.deleted = false AND d.documentType = :type AND d.documentNumber = :number AND d.documentIssuingCountry = :issuingCountry and d.active = true")
    Optional<Traveller> findByDocumentTypeAndNumberAndIssuingCountryAndActive(@Param("type") DocumentType type, @Param("number") String number, @Param("issuingCountry") String issuingCountry);



}
