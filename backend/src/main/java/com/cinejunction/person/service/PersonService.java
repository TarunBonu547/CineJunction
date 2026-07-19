package com.cinejunction.person.service;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
import com.cinejunction.person.dto.PersonRequest;
import com.cinejunction.person.dto.PersonResponse;
import com.cinejunction.person.dto.PersonSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonService {

    PersonResponse createPerson(PersonRequest request);

    PersonResponse getPersonById(Long id);

    Page<PersonSummaryResponse> getAllPeople(Pageable pageable);

    Page<PersonSummaryResponse> searchPeople(String keyword, Pageable pageable);

    Page<PersonSummaryResponse> getFilteredPeople(
            Department department,
            Gender gender,
            String nationality,
            Boolean adult,
            Double minPopularity,
            Integer birthYear,
            Pageable pageable);

    PersonResponse updatePerson(Long id, PersonRequest request);

    void deletePerson(Long id);
}
