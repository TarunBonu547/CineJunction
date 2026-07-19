package com.cinejunction.person.mapper;

import com.cinejunction.person.dto.PersonRequest;
import com.cinejunction.person.dto.PersonResponse;
import com.cinejunction.person.dto.PersonSummaryResponse;
import com.cinejunction.person.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person toEntity(PersonRequest request);

    PersonResponse toResponse(Person person);

    PersonSummaryResponse toSummary(Person person);

    List<PersonSummaryResponse> toSummaryList(List<Person> people);
}
