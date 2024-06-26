package com.bielcik.traveller.service.dto;

import com.bielcik.traveller.domain.DocumentType;
import lombok.Data;

@Data
public class DocumentSearchDTO {
    private DocumentType documentType;
    private String documentNumber;
    private String documentIssuingCountry;
}
