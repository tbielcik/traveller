package com.bielcik.traveller.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravellerDocumentPK {
    DocumentType documentType;
    String documentNumber;
    String documentIssuingCountry;
}
