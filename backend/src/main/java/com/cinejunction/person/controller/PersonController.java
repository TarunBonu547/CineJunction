package com.cinejunction.person.controller;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
import com.cinejunction.person.dto.PersonRequest;
import com.cinejunction.person.dto.PersonResponse;
import com.cinejunction.person.dto.PersonSummaryResponse;
import com.cinejunction.person.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/people")
@RequiredArgsConstructor
@Tag(name = "People", description = "People management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PersonController {

    private final PersonService personService;

    @PostMapping
    @Operation(summary = "Create a new person", description = "Creates a new person with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Person created successfully", content = @Content(schema = @Schema(implementation = PersonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Person with the same name already exists")
    })
    public ResponseEntity<PersonResponse> createPerson(@Valid @RequestBody PersonRequest request) {
        PersonResponse response = personService.createPerson(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all people", description = "Retrieves all people with pagination, sorting, and filtering", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "name,asc")),
            @Parameter(name = "department", description = "Filter by department", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"ACTOR", "DIRECTOR", "WRITER", "PRODUCER", "MUSIC", "EDITOR", "CINEMATOGRAPHY"})),
            @Parameter(name = "gender", description = "Filter by gender", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"MALE", "FEMALE", "OTHER"})),
            @Parameter(name = "nationality", description = "Filter by nationality", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "adult", description = "Filter by adult content flag", in = ParameterIn.QUERY, schema = @Schema(type = "boolean")),
            @Parameter(name = "minPopularity", description = "Filter by minimum popularity", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "double")),
            @Parameter(name = "birthYear", description = "Filter by birth year", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "People retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<PersonSummaryResponse>> getAllPeople(
            @Parameter(hidden = true) Pageable pageable,
            @RequestParam(required = false) Department department,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) Boolean adult,
            @RequestParam(required = false) Double minPopularity,
            @RequestParam(required = false) Integer birthYear) {
        Page<PersonSummaryResponse> people = personService.getFilteredPeople(
                department, gender, nationality, adult, minPopularity, birthYear, pageable);
        return ResponseEntity.ok(people);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get person by ID", description = "Retrieves a person by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person found", content = @Content(schema = @Schema(implementation = PersonResponse.class))),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<PersonResponse> getPersonById(@PathVariable Long id) {
        PersonResponse response = personService.getPersonById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a person", description = "Updates an existing person by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person updated successfully", content = @Content(schema = @Schema(implementation = PersonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Person not found"),
            @ApiResponse(responseCode = "409", description = "Person with the same name already exists")
    })
    public ResponseEntity<PersonResponse> updatePerson(@PathVariable Long id, @Valid @RequestBody PersonRequest request) {
        PersonResponse response = personService.updatePerson(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a person", description = "Deletes a person by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Person deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search people", description = "Searches people by name keyword with pagination", parameters = {
            @Parameter(name = "keyword", description = "Search keyword for person name", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "Christopher")),
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "name,asc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<PersonSummaryResponse>> searchPeople(
            @RequestParam String keyword,
            @Parameter(hidden = true) Pageable pageable) {
        Page<PersonSummaryResponse> people = personService.searchPeople(keyword, pageable);
        return ResponseEntity.ok(people);
    }
}
