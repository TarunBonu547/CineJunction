package com.cinejunction.person.dto;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PersonRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    @Size(max = 5000, message = "Biography must not exceed 5000 characters")
    private String biography;

    private LocalDate birthDate;

    private LocalDate deathDate;

    @Size(max = 100, message = "Place of birth must not exceed 100 characters")
    private String placeOfBirth;

    @Size(max = 100, message = "Nationality must not exceed 100 characters")
    private String nationality;

    private Gender gender;

    private Department department;

    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImageUrl;

    @DecimalMin(value = "0", message = "Popularity cannot be negative")
    private Double popularity;

    private Boolean adult;
}
