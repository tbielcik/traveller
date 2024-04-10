package com.bielcik.traveller.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Traveller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long travellerId;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String mobileNumber;

    @OneToMany(mappedBy = "traveller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravellerDocument> travellerDocuments = new ArrayList<>();

    private boolean deleted;
}
