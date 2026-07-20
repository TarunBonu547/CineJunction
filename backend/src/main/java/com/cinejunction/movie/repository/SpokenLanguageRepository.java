package com.cinejunction.movie.repository;

import com.cinejunction.movie.entity.SpokenLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpokenLanguageRepository extends JpaRepository<SpokenLanguage, Long> {

    Optional<SpokenLanguage> findByIso6391(String iso6391);

    boolean existsByIso6391(String iso6391);
}
