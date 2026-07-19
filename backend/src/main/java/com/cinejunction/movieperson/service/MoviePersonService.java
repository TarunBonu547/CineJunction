package com.cinejunction.movieperson.service;

import com.cinejunction.movieperson.dto.MoviePersonRequest;
import com.cinejunction.movieperson.dto.MoviePersonResponse;
import com.cinejunction.movieperson.dto.MoviePersonSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MoviePersonService {

    MoviePersonResponse assignPersonToMovie(MoviePersonRequest request);

    MoviePersonResponse updateRelationship(Long id, MoviePersonRequest request);

    void deleteRelationship(Long id);

    List<MoviePersonSummaryResponse> getMovieCast(Long movieId);

    List<MoviePersonSummaryResponse> getMovieCrew(Long movieId);

    Page<MoviePersonSummaryResponse> getPersonFilmography(Long personId, Pageable pageable);
}
