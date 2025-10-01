package com.stas_kozh.companyservice.controller;

import com.stas_kozh.companyservice.serivce.CompanyService;
import com.stas_kozh.core.CompanyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @GetMapping
    public String greetingMessage() {
        return service.greeting();
    }

    @GetMapping("/{companyId}")
    public CompanyResponseDto getCompanyNameById(
            @PathVariable Long companyId
    ) {
        return service.getCompanyNameById(companyId);
    }
}
