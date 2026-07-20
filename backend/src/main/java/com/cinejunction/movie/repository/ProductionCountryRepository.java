package com.cinejunction.movie.repository;

import com.cinejunction.movie.entity.ProductionCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionCountryRepository extends JpaRepository<ProductionCountry, Long> {

    Optional<ProductionCountry> findByIsoCode(String isoCode);

    boolean existsByIsoCode(String isoCode);
}
