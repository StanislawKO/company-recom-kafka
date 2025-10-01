package com.stas_kozh.companyservice.repository;

import com.stas_kozh.companyservice.model.Company;
import com.stas_kozh.core.CompanyResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Company findByMessageId (String messageId);
}
