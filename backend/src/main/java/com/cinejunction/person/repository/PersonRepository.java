package com.cinejunction.person.repository;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
import com.cinejunction.person.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    Optional<Person> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    Page<Person> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Person> findByDepartment(Department department, Pageable pageable);

    Page<Person> findByNationalityIgnoreCase(String nationality, Pageable pageable);
}
