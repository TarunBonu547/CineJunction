package com.cinejunction.movie.specification;

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
}
