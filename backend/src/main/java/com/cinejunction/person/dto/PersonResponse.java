package com.cinejunction.person.dto;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
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
public class PersonResponse {

    private Long id;
    private String name;
    private Department department;
    private Gender gender;
    private String nationality;
    private LocalDate birthDate;
    private String profileImageUrl;
    private Double popularity;
    private Boolean adult;
    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;
}
