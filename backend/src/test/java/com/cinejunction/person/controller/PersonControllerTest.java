package com.cinejunction.person.controller;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
import com.cinejunction.exception.GlobalExceptionHandler;
import com.cinejunction.exception.PersonNotFoundException;
import com.cinejunction.person.dto.PersonRequest;
import com.cinejunction.person.dto.PersonResponse;
import com.cinejunction.person.dto.PersonSummaryResponse;
import com.cinejunction.person.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class PersonControllerTest {

    private MockMvc mockMvc;
    private PersonService personService;
    private ObjectMapper objectMapper;
    private PersonController personController;

    @BeforeEach
    void setUp() {
        personService = Mockito.mock(PersonService.class);
        personController = new PersonController(personService);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(personController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createPerson_ReturnsCreated() throws Exception {
        PersonRequest request = new PersonRequest();
        request.setName("Christopher Nolan");
        request.setBiography("British-American filmmaker");
        request.setBirthDate(LocalDate.of(1970, 7, 30));
        request.setNationality("British");
        request.setGender(Gender.MALE);
        request.setDepartment(Department.DIRECTOR);
        request.setPopularity(85.0);
        request.setAdult(false);

        PersonResponse response = new PersonResponse();
        response.setId(1L);
        response.setName("Christopher Nolan");
        response.setDepartment(Department.DIRECTOR);

        Mockito.when(personService.createPerson(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Christopher Nolan"));
    }

    @Test
    void createPerson_InvalidRequest_ReturnsBadRequest() throws Exception {
        PersonRequest request = new PersonRequest();
        request.setName("");

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllPeople_ReturnsPage() throws Exception {
        PersonSummaryResponse summary = new PersonSummaryResponse();
        summary.setId(1L);
        summary.setName("Christopher Nolan");
        summary.setDepartment(Department.DIRECTOR);

        Page<PersonSummaryResponse> page = new PageImpl<>(List.of(summary), PageRequest.of(0, 1), 1);
        Mockito.when(personService.getFilteredPeople(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/people"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Christopher Nolan"));
    }

    @Test
    void getPersonById_ReturnsPerson() throws Exception {
        PersonResponse response = new PersonResponse();
        response.setId(1L);
        response.setName("Christopher Nolan");

        Mockito.when(personService.getPersonById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/people/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Christopher Nolan"));
    }

    @Test
    void getPersonById_NotFound_Returns404() throws Exception {
        Mockito.when(personService.getPersonById(1L)).thenThrow(new PersonNotFoundException("Person not found with id: 1"));

        mockMvc.perform(get("/api/v1/people/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePerson_ReturnsUpdatedPerson() throws Exception {
        PersonRequest request = new PersonRequest();
        request.setName("Christopher Nolan Updated");
        request.setDepartment(Department.DIRECTOR);

        PersonResponse response = new PersonResponse();
        response.setId(1L);
        response.setName("Christopher Nolan Updated");

        Mockito.when(personService.updatePerson(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/people/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Christopher Nolan Updated"));
    }

    @Test
    void deletePerson_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/people/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchPeople_ReturnsFilteredPage() throws Exception {
        PersonSummaryResponse summary = new PersonSummaryResponse();
        summary.setId(1L);
        summary.setName("Christopher Nolan");

        Page<PersonSummaryResponse> page = new PageImpl<>(List.of(summary), PageRequest.of(0, 1), 1);
        Mockito.when(personService.searchPeople(eq("Christopher"), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/people/search")
                        .param("keyword", "Christopher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Christopher Nolan"));
    }
}
