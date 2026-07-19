package com.cinejunction.rating.mapper;

import com.cinejunction.rating.dto.CreateRatingRequest;
import com.cinejunction.rating.dto.RatingResponse;
import com.cinejunction.rating.entity.MovieRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MovieRatingMapper {

    MovieRatingMapper INSTANCE = Mappers.getMapper(MovieRatingMapper.class);

    MovieRating toEntity(CreateRatingRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.title", target = "movieTitle")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    RatingResponse toResponse(MovieRating movieRating);
}
