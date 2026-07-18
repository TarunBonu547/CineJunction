package com.cinejunction.cast.repository;

import com.cinejunction.cast.entity.CastMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link CastMember} entities.
 */
@Repository
public interface CastMemberRepository extends JpaRepository<CastMember, Long> {

    /**
     * Finds all cast members for a given movie, ordered by cast order ascending.
     *
     * @param movieId the movie ID
     * @return a list of {@link CastMember} entities
     */
    List<CastMember> findByMovieIdOrderByCastOrderAsc(Long movieId);

    /**
     * Finds all cast members for a given person.
     *
     * @param personId the person ID
     * @return a list of {@link CastMember} entities
     */
    List<CastMember> findByPersonId(Long personId);
}
