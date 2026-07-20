package com.cinejunction.tmdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewMemberDto {

    private Long id;
    private String name;
    private String department;
    private String job;
    private String profilePath;
}
