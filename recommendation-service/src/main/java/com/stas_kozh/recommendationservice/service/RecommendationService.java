package com.stas_kozh.recommendationservice.service;

import com.stas_kozh.core.CompanyResponseDto;
import com.stas_kozh.core.RecommendationResponseDto;
import com.stas_kozh.recommendationservice.dto.PositionResponseDto;
import com.stas_kozh.recommendationservice.dto.RecommendationMapper;
import com.stas_kozh.recommendationservice.model.Recommendation;
import com.stas_kozh.recommendationservice.repsitory.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

//    private final ServiceClient serviceClient;
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;

    public String greeting() {
        return "Welcome to the recommendation-service!";
    }

    public RecommendationResponseDto getRecommendationById(Long id) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Recommendation not found"));
        return recommendationMapper.toDto(recommendation);
    }

    public Page<RecommendationResponseDto> getAllRecommendation(Pageable pageable) {
        return recommendationRepository.findAll(pageable)
                .map(recommendationMapper::toDto);
    }

    public RecommendationResponseDto createRecommendation(RecommendationResponseDto recommendationResponseDto) {
        return Optional.ofNullable(recommendationResponseDto)
                .map(recommendationMapper::toEntity)
                .map(recommendationRepository::saveAndFlush)
                .map(recommendationMapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Error save recommendation"));
    }

    @Transactional
    public void updateRecommendation(Long id, RecommendationResponseDto recommendationResponseDto) {
        Recommendation recommendationUpdate = recommendationMapper.toEntity(recommendationResponseDto);
        Recommendation recommendation = recommendationRepository.findById(id).orElseThrow();
        recommendation.setFullName(recommendationUpdate.getFullName());
        recommendation.setCompanyId(recommendationUpdate.getCompanyId());
        recommendation.setPositionId(recommendationUpdate.getPositionId());
        recommendation.setRecommendation(recommendationUpdate.getRecommendation());
        recommendationRepository.save(recommendation);
    }
}
