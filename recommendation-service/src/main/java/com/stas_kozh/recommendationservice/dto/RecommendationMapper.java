package com.stas_kozh.recommendationservice.dto;

import com.stas_kozh.core.RecommendationResponseDto;
import com.stas_kozh.recommendationservice.model.Recommendation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    RecommendationResponseDto toDto(Recommendation recommendation);
    Recommendation toEntity(RecommendationResponseDto recommendationResponseDto);
}
