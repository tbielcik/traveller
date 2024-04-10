package com.bielcik.traveller.service.mapper;


import com.bielcik.traveller.domain.TravellerDocument;
import com.bielcik.traveller.service.dto.TravellerDocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface TravellerDocumentMapper {

    TravellerDocument toEntity(TravellerDocumentDTO dto);

    @Mapping(source = "traveller.travellerId", target = "travellerId")
    TravellerDocumentDTO toDto(TravellerDocument entity);
}
