package com.cinejunction.person.repository;

import com.cinejunction.person.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Person} entities.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /**
     * Finds persons whose name contains the given keyword, ignoring case.
     *
     * @param keyword  the search keyword
     * @param pageable pagination information
     * @return a {@link Page} of matching persons
     */
    Page<Person> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
