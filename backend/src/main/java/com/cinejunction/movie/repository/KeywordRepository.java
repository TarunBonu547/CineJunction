package com.cinejunction.movie.repository;

import com.cinejunction.movie.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Optional<Keyword> findByTmdbId(Long tmdbId);

    boolean existsByTmdbId(Long tmdbId);
}
