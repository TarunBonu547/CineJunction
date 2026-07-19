package com.cinejunction.person.dto;

import com.cinejunction.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonSummaryResponse {

    private Long id;
    private String name;
    private Department department;
    private String profileImageUrl;
    private Double popularity;
    private LocalDate birthDate;
}
