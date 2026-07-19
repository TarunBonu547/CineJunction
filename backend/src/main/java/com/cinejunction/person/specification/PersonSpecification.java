package com.cinejunction.person.specification;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
import com.cinejunction.person.entity.Person;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PersonSpecification {

    private PersonSpecification() {
    }

    public static Specification<Person> withFilters(
            Department department,
            Gender gender,
            String nationality,
            Boolean adult,
            Double minPopularity,
            Integer birthYear) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (department != null) {
                predicates.add(cb.equal(root.get("department"), department));
            }

            if (gender != null) {
                predicates.add(cb.equal(root.get("gender"), gender));
            }

            if (StringUtils.hasText(nationality)) {
                predicates.add(cb.equal(cb.lower(root.get("nationality")), nationality.toLowerCase()));
            }

            if (adult != null) {
                predicates.add(cb.equal(root.get("adult"), adult));
            }

            if (minPopularity != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("popularity"), minPopularity));
            }

            if (birthYear != null) {
                LocalDate startOfYear = LocalDate.of(birthYear, 1, 1);
                LocalDate startOfNextYear = LocalDate.of(birthYear + 1, 1, 1);
                predicates.add(cb.between(root.get("birthDate"), startOfYear, startOfNextYear));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
