package com.bielcik.traveller.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@IdClass(TravellerDocumentPK.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TravellerDocument {

    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    @Id
    @Column(nullable = false)
    private String documentNumber;
    @Id
    @Column(nullable = false)
    private String documentIssuingCountry;

    @ManyToOne
    @JoinColumn(name = "travellerId")
    private Traveller traveller;

    @Column
    private boolean active;
}
