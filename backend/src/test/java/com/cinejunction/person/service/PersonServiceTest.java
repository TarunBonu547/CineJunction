package com.cinejunction.person.service;

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
import com.cinejunction.person.service.impl.PersonServiceImpl;
import com.cinejunction.person.specification.PersonSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonServiceImpl personService;

    private Person person;
    private PersonRequest personRequest;
    private PersonResponse personResponse;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(1L);
        person.setName("Christopher Nolan");
        person.setBiography("British-American filmmaker");
        person.setBirthDate(LocalDate.of(1970, 7, 30));
        person.setNationality("British");
        person.setGender(Gender.MALE);
        person.setDepartment(Department.DIRECTOR);
        person.setPopularity(85.0);
        person.setAdult(false);

        personRequest = new PersonRequest();
        personRequest.setName("Christopher Nolan");
        personRequest.setBiography("British-American filmmaker");
        personRequest.setBirthDate(LocalDate.of(1970, 7, 30));
        personRequest.setNationality("British");
        personRequest.setGender(Gender.MALE);
        personRequest.setDepartment(Department.DIRECTOR);
        personRequest.setPopularity(85.0);
        personRequest.setAdult(false);

        personResponse = new PersonResponse();
        personResponse.setId(1L);
        personResponse.setName("Christopher Nolan");
        personResponse.setDepartment(Department.DIRECTOR);
        personResponse.setGender(Gender.MALE);
        personResponse.setNationality("British");
        personResponse.setBirthDate(LocalDate.of(1970, 7, 30));
        personResponse.setPopularity(85.0);
        personResponse.setAdult(false);
    }

    @Test
    void createPerson_Success() {
        when(personRepository.existsByNameIgnoreCase("Christopher Nolan")).thenReturn(false);
        when(personMapper.toEntity(any())).thenReturn(person);
        when(personRepository.save(any())).thenReturn(person);
        when(personMapper.toResponse(any())).thenReturn(personResponse);

        PersonResponse response = personService.createPerson(personRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Christopher Nolan");
        verify(personRepository).save(any());
    }

    @Test
    void createPerson_DuplicateName_ThrowsException() {
        when(personRepository.existsByNameIgnoreCase("Christopher Nolan")).thenReturn(true);

        assertThrows(PersonAlreadyExistsException.class, () -> personService.createPerson(personRequest));
    }

    @Test
    void getPersonById_Success() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personMapper.toResponse(any())).thenReturn(personResponse);

        PersonResponse response = personService.getPersonById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Christopher Nolan");
    }

    @Test
    void getPersonById_NotFound_ThrowsException() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.getPersonById(1L));
    }

    @Test
    void getAllPeople_ReturnsPage() {
        Page<Person> personPage = new PageImpl<>(List.of(person));
        when(personRepository.findAll(any(Pageable.class))).thenReturn(personPage);
        when(personMapper.toSummary(any())).thenReturn(new PersonSummaryResponse());

        Page<PersonSummaryResponse> result = personService.getAllPeople(Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchPeople_ReturnsFilteredPage() {
        Page<Person> personPage = new PageImpl<>(List.of(person));
        when(personRepository.findByNameContainingIgnoreCase(eq("Christopher"), any(Pageable.class))).thenReturn(personPage);
        when(personMapper.toSummary(any())).thenReturn(new PersonSummaryResponse());

        Page<PersonSummaryResponse> result = personService.searchPeople("Christopher", Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void updatePerson_Success() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.save(any())).thenReturn(person);
        when(personMapper.toResponse(any())).thenReturn(personResponse);

        PersonResponse response = personService.updatePerson(1L, personRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Christopher Nolan");
        verify(personRepository).save(person);
    }

    @Test
    void updatePerson_NotFound_ThrowsException() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.updatePerson(1L, personRequest));
    }

    @Test
    void updatePerson_DuplicateName_ThrowsException() {
        Person existing = new Person();
        existing.setId(2L);
        existing.setName("Steven Spielberg");

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(personRepository.findByNameIgnoreCase("Christopher Nolan")).thenReturn(Optional.of(existing));

        assertThrows(PersonAlreadyExistsException.class, () -> personService.updatePerson(1L, personRequest));
    }

    @Test
    void deletePerson_Success() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        personService.deletePerson(1L);

        verify(personRepository).delete(person);
    }

    @Test
    void deletePerson_NotFound_ThrowsException() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.deletePerson(1L));
    }

    @Test
    void createPerson_WithFutureBirthDate_ThrowsException() {
        personRequest.setBirthDate(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> personService.createPerson(personRequest));
    }

    @Test
    void createPerson_WithNegativePopularity_ThrowsException() {
        personRequest.setPopularity(-1.0);

        assertThrows(IllegalArgumentException.class, () -> personService.createPerson(personRequest));
    }
}
