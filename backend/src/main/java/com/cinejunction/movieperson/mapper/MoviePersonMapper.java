package com.cinejunction.movieperson.mapper;

import com.cinejunction.movieperson.dto.MoviePersonRequest;
import com.cinejunction.movieperson.dto.MoviePersonResponse;
import com.cinejunction.movieperson.dto.MoviePersonSummaryResponse;
import com.cinejunction.movieperson.entity.MoviePerson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MoviePersonMapper {

    MoviePersonMapper INSTANCE = Mappers.getMapper(MoviePersonMapper.class);

    MoviePerson toEntity(MoviePersonRequest request);

    @Mapping(source = "id", target = "relationshipId")
    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.title", target = "movieTitle")
    @Mapping(source = "person.id", target = "personId")
    @Mapping(source = "person.name", target = "personName")
    @Mapping(source = "person.department.displayName", target = "department")
    MoviePersonResponse toResponse(MoviePerson moviePerson);

    @Mapping(source = "id", target = "relationshipId")
    @Mapping(source = "person.id", target = "personId")
    @Mapping(source = "person.name", target = "personName")
    @Mapping(source = "person.department.displayName", target = "department")
    MoviePersonSummaryResponse toSummary(MoviePerson moviePerson);

    List<MoviePersonSummaryResponse> toSummaryList(List<MoviePerson> moviePeople);
}
