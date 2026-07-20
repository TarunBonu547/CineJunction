package com.cinejunction.movie.repository;

import com.cinejunction.movie.entity.ProductionCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionCompanyRepository extends JpaRepository<ProductionCompany, Long> {

    Optional<ProductionCompany> findByTmdbId(Long tmdbId);

    boolean existsByTmdbId(Long tmdbId);
}
