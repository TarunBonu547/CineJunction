package com.cinejunction.person.service.impl;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
import com.cinejunction.exception.PersonAlreadyExistsException;
import com.cinejunction.exception.PersonNotFoundException;
import com.cinejunction.person.dto.PersonRequest;
import com.cinejunction.person.dto.PersonResponse;
import com.cinejunction.person.dto.PersonSummaryResponse;
import com.cinejunction.person.entity.Person;
import com.cinejunction.person.mapper.PersonMapper;
import com.cinejunction.person.repository.PersonRepository;
import com.cinejunction.person.service.PersonService;
import com.cinejunction.person.specification.PersonSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Override
    @Transactional
    public PersonResponse createPerson(PersonRequest request) {
        String trimmedName = request.getName().trim();
        if (personRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new PersonAlreadyExistsException("Person already exists with name: " + trimmedName);
        }

        validateDates(request.getBirthDate(), request.getDeathDate());
        validatePopularity(request.getPopularity());

        Person person = personMapper.toEntity(request);
        person.setName(trimmedName);
        trimStringFields(request, person);
        personRepository.save(person);

        return personMapper.toResponse(person);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonResponse getPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
        return personMapper.toResponse(person);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonSummaryResponse> getAllPeople(Pageable pageable) {
        return personRepository.findAll(pageable).map(personMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonSummaryResponse> searchPeople(String keyword, Pageable pageable) {
        return personRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(personMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonSummaryResponse> getFilteredPeople(
            Department department,
            Gender gender,
            String nationality,
            Boolean adult,
            Double minPopularity,
            Integer birthYear,
            Pageable pageable) {

        return personRepository.findAll(PersonSpecification.withFilters(
                department, gender, nationality, adult, minPopularity, birthYear), pageable)
                .map(personMapper::toSummary);
    }

    @Override
    @Transactional
    public PersonResponse updatePerson(Long id, PersonRequest request) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));

        String trimmedName = request.getName().trim();
        personRepository.findByNameIgnoreCase(trimmedName).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new PersonAlreadyExistsException("Person already exists with name: " + trimmedName);
            }
        });

        validateDates(request.getBirthDate(), request.getDeathDate());
        validatePopularity(request.getPopularity());

        person.setName(trimmedName);
        trimStringFields(request, person);
        personRepository.save(person);

        return personMapper.toResponse(person);
    }

    @Override
    @Transactional
    public void deletePerson(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
        personRepository.delete(person);
    }

    private void validateDates(LocalDate birthDate, LocalDate deathDate) {
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        if (birthDate != null && deathDate != null && deathDate.isBefore(birthDate)) {
            throw new IllegalArgumentException("Death date cannot be before birth date");
        }
    }

    private void validatePopularity(Double popularity) {
        if (popularity != null && popularity < 0) {
            throw new IllegalArgumentException("Popularity cannot be negative");
        }
    }

    private void trimStringFields(PersonRequest request, Person person) {
        if (request.getBiography() != null) {
            person.setBiography(request.getBiography().trim());
        }
        if (request.getPlaceOfBirth() != null) {
            person.setPlaceOfBirth(request.getPlaceOfBirth().trim());
        }
        if (request.getNationality() != null) {
            person.setNationality(request.getNationality().trim());
        }
        if (request.getProfileImageUrl() != null) {
            person.setProfileImageUrl(request.getProfileImageUrl().trim());
        }
    }
}
