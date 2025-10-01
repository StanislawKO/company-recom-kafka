package com.stas_kozh.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyGettingEvent {
    private Long id;
    private String company;
}
