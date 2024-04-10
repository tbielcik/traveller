package com.bielcik.traveller.service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
public class TravellerDTO {
    private Long travellerId;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    private String email;
    private String mobileNumber;

    private List<TravellerDocumentDTO> travellerDocuments = new ArrayList<>();
    private boolean deleted;
}
