package com.stas_kozh.companyservice.serivce;

import com.stas_kozh.companyservice.dto.CompanyMapper;
import com.stas_kozh.companyservice.model.Company;
import com.stas_kozh.companyservice.repository.CompanyRepository;
import com.stas_kozh.core.CompanyResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {
    private final CompanyRepository repository;
    private final CompanyMapper companyMapper;

    public String greeting() {
        return "Welcome to the company service!";
    }

    public CompanyResponseDto getCompanyNameById(Long id) {
        return repository.findById(id)
                .map(companyMapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Company not found"));
    }

    public CompanyResponseDto getCompanyByMessageId(String messageId) {
        return companyMapper.toDto(repository.findByMessageId(messageId));
    }

    public Company createCompany(String messageId, String companyName, Long firstCapital) {
        return repository.save(new Company(messageId, companyName, firstCapital));
    }
}
