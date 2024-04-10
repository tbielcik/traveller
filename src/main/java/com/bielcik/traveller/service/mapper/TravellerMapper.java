package com.bielcik.traveller.service.mapper;


import com.bielcik.traveller.domain.Traveller;
import com.bielcik.traveller.service.dto.TravellerDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TravellerMapper {
    Traveller toEntity(TravellerDTO dto);

    TravellerDTO toDto(Traveller entity);
}
