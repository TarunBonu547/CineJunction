package com.cinejunction.person.repository;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
import com.cinejunction.person.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    private Person createPerson(String name) {
        Person person = new Person();
        person.setName(name);
        person.setBiography("Test biography");
        person.setBirthDate(LocalDate.of(1970, 1, 1));
        person.setNationality("American");
        person.setGender(Gender.MALE);
        person.setDepartment(Department.ACTOR);
        person.setPopularity(50.0);
        person.setAdult(false);
        java.time.Instant now = java.time.Instant.now();
        person.setCreatedAt(now);
        person.setUpdatedAt(now);
        return person;
    }

    @Test
    void savePerson_ReturnsSavedPerson() {
        Person person = createPerson("Christopher Nolan");
        Person saved = personRepository.save(person);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Christopher Nolan");
    }

    @Test
    void findByNameIgnoreCase_ReturnsPerson() {
        Person person = createPerson("Christopher Nolan");
        personRepository.save(person);

        Optional<Person> found = personRepository.findByNameIgnoreCase("Christopher Nolan");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Christopher Nolan");
    }

    @Test
    void existsByNameIgnoreCase_ReturnsTrue() {
        Person person = createPerson("Christopher Nolan");
        personRepository.save(person);

        boolean exists = personRepository.existsByNameIgnoreCase("Christopher Nolan");

        assertThat(exists).isTrue();
    }

    @Test
    void findByNameContainingIgnoreCase_ReturnsPage() {
        Person person1 = createPerson("Christopher Nolan");
        Person person2 = createPerson("Christopher Plummer");

        personRepository.save(person1);
        personRepository.save(person2);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Person> result = personRepository.findByNameContainingIgnoreCase("Christopher", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findByDepartment_ReturnsPage() {
        Person actor = createPerson("Tom Hanks");
        actor.setDepartment(Department.ACTOR);
        Person director = createPerson("Steven Spielberg");
        director.setDepartment(Department.DIRECTOR);

        personRepository.save(actor);
        personRepository.save(director);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Person> result = personRepository.findByDepartment(Department.ACTOR, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDepartment()).isEqualTo(Department.ACTOR);
    }

    @Test
    void findByNationalityIgnoreCase_ReturnsPage() {
        Person person1 = createPerson("Tom Hanks");
        person1.setNationality("American");
        Person person2 = createPerson("Christian Bale");
        person2.setNationality("British");

        personRepository.save(person1);
        personRepository.save(person2);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Person> result = personRepository.findByNationalityIgnoreCase("American", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNationality()).isEqualTo("American");
    }
}
