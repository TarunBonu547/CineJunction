package com.cinejunction.movieperson.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoviePersonResponse {

    private Long relationshipId;
    private Long movieId;
    private String movieTitle;
    private Long personId;
    private String personName;
    private String department;
    private com.cinejunction.movieperson.enums.RoleType roleType;
    private String characterName;
    private String creditedAs;
    private Integer billingOrder;
}
