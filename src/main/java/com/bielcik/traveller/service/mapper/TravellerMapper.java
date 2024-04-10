package com.bielcik.traveller.service.mapper;


import com.bielcik.traveller.domain.Traveller;
import com.bielcik.traveller.service.dto.TravellerDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", uses = TravellerDocumentMapper.class)
public interface TravellerMapper {

    Traveller toEntity(TravellerDTO dto);

    @AfterMapping
    default void calledWithSourceAndTarget(TravellerDTO source, @MappingTarget Traveller target) {
        target.getTravellerDocuments().forEach(travellerDocument -> travellerDocument.setTraveller(target));

    }

    TravellerDTO toDto(Traveller entity);
}
