package com.stas_kozh.companyservice.dto;

import com.stas_kozh.companyservice.model.Company;
import com.stas_kozh.core.CompanyResponseDto;
import com.stas_kozh.core.RecommendationWithCompanyResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyResponseDto toDto(Company stage);

    Company toEntity(CompanyResponseDto stageDto);

    RecommendationWithCompanyResponseDto toForCreateDto(CompanyResponseDto stageDto);
}
