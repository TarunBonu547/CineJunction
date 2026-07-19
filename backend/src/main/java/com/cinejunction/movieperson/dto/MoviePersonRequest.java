package com.cinejunction.movieperson.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MoviePersonRequest {

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Person ID is required")
    private Long personId;

    @NotNull(message = "Role type is required")
    private com.cinejunction.movieperson.enums.RoleType roleType;

    @Size(max = 200, message = "Character name must not exceed 200 characters")
    private String characterName;

    @Size(max = 200, message = "Credited as must not exceed 200 characters")
    private String creditedAs;

    @NotNull(message = "Billing order is required")
    @Min(value = 0, message = "Billing order must be >= 0")
    private Integer billingOrder;
}
