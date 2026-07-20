package com.cinejunction.movie.entity;

import com.cinejunction.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "production_countries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"iso_code"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductionCountry extends BaseEntity {

    @Column(name = "iso_code", nullable = false, length = 10, unique = true)
    private String isoCode;

    @Column(nullable = false, length = 255)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductionCountry that) || getId() == null) return false;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ProductionCountry{" +
                "id=" + getId() +
                ", isoCode='" + isoCode + '\'' +
                '}';
    }
}
