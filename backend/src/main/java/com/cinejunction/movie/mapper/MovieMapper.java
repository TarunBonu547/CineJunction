package com.cinejunction.movie.mapper;

import com.cinejunction.movie.dto.MovieRequest;
import com.cinejunction.movie.dto.MovieResponse;
import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.movie.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for converting between {@link Movie} entities and movie DTOs.
 */
@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    /**
     * Converts a {@link MovieRequest} to a new {@link Movie} entity.
     *
     * @param request the movie request DTO
     * @return a new Movie entity
     */
    Movie toEntity(MovieRequest request);

    /**
     * Converts a {@link Movie} entity to a {@link MovieResponse} DTO.
     *
     * @param movie the movie entity
     * @return the movie response DTO
     */
    @Mapping(target = "genres", ignore = true)
    MovieResponse toResponse(Movie movie);

    /**
     * Converts a {@link Movie} entity to a {@link MovieSummaryResponse} DTO.
     *
     * @param movie the movie entity
     * @return the movie summary response DTO
     */
    MovieSummaryResponse toSummary(Movie movie);

    /**
     * Converts a list of {@link Movie} entities to a list of {@link MovieSummaryResponse} DTOs.
     *
     * @param movies the list of movie entities
     * @return the list of movie summary response DTOs
     */
    List<MovieSummaryResponse> toSummaryList(List<Movie> movies);

    /**
     * Updates an existing {@link Movie} entity from a {@link MovieRequest} DTO.
     *
     * @param request the movie request DTO
     * @param movie   the existing movie entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "castMembers", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "voteCount", ignore = true)
    @Mapping(target = "popularity", ignore = true)
    void updateMovieFromRequest(MovieRequest request, @MappingTarget Movie movie);
}
