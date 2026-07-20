package com.cinejunction.movie.repository;

import com.cinejunction.movie.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    Optional<Collection> findByTmdbId(Long tmdbId);

    boolean existsByTmdbId(Long tmdbId);
}
