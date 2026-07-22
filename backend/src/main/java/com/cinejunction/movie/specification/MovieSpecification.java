package com.cinejunction.movie.specification;

import com.cinejunction.movie.dto.search.MovieSearchRequest;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.enums.MovieStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specifications for filtering {@link Movie} entities.
 */
public final class MovieSpecification {

    private MovieSpecification() {
    }

    /**
     * Builds a Specification from individual filter parameters.
     */
    public static Specification<Movie> withFilters(
            String genre,
            String language,
            Integer year,
            MovieStatus status,
            BigDecimal minRating,
            Integer maxRuntime,
            Boolean adult) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(genre)) {
                Join<Object, Object> genreJoin = root.join("genres", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(genreJoin.get("name")), genre.toLowerCase()));
            }

            if (StringUtils.hasText(language)) {
                predicates.add(cb.equal(cb.lower(root.get("language")), language.toLowerCase()));
            }

            if (year != null) {
                LocalDate startOfYear = LocalDate.of(year, 1, 1);
                LocalDate startOfNextYear = LocalDate.of(year + 1, 1, 1);
                predicates.add(cb.between(root.get("releaseDate"), startOfYear, startOfNextYear));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), minRating));
            }

            if (maxRuntime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("runtime"), maxRuntime));
            }

            if (adult != null) {
                predicates.add(cb.equal(root.get("adult"), adult));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Builds a Specification from an advanced search request DTO.
     */
    public static Specification<Movie> withAdvancedSearch(MovieSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), keyword);
                Predicate overviewLike = cb.like(cb.lower(root.get("overview")), keyword);
                predicates.add(cb.or(titleLike, overviewLike));
            }

            if (StringUtils.hasText(request.getGenre())) {
                Join<Object, Object> genreJoin = root.join("genres", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(genreJoin.get("name")), request.getGenre().toLowerCase()));
            }

            if (StringUtils.hasText(request.getLanguage())) {
                predicates.add(cb.equal(cb.lower(root.get("language")), request.getLanguage().toLowerCase()));
            }

            if (request.getYear() != null) {
                LocalDate startOfYear = LocalDate.of(request.getYear(), 1, 1);
                LocalDate startOfNextYear = LocalDate.of(request.getYear() + 1, 1, 1);
                predicates.add(cb.between(root.get("releaseDate"), startOfYear, startOfNextYear));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), request.getMinRating()));
            }

            if (request.getMaxRating() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("averageRating"), request.getMaxRating()));
            }

            if (request.getMinRuntime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("runtime"), request.getMinRuntime()));
            }

            if (request.getMaxRuntime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("runtime"), request.getMaxRuntime()));
            }

            if (request.getAdult() != null) {
                predicates.add(cb.equal(root.get("adult"), request.getAdult()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Builds a Specification for autocomplete suggestions.
     */
    public static Specification<Movie> withSuggestionKeyword(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            Predicate titleLike = cb.like(cb.lower(root.get("title")), pattern);
            Predicate overviewLike = cb.like(cb.lower(root.get("overview")), pattern);
            return cb.or(titleLike, overviewLike);
        };
    }
}
