package com.bielcik.traveller.domain;

import lombok.Value;

@Value
public class TravellerPK {
    Long travellerId;
    String email;
    String mobileNumber;
}
