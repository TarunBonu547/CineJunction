package com.cinejunction.review.mapper;

import com.cinejunction.review.dto.CreateReviewRequest;
import com.cinejunction.review.dto.ReviewResponse;
import com.cinejunction.review.entity.MovieReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MovieReviewMapper {

    MovieReviewMapper INSTANCE = Mappers.getMapper(MovieReviewMapper.class);

    MovieReview toEntity(CreateReviewRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.title", target = "movieTitle")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    ReviewResponse toResponse(MovieReview movieReview);
}
