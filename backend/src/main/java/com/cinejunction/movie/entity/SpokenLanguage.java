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
        name = "spoken_languages",
        uniqueConstraints = @UniqueConstraint(columnNames = {"iso_6391"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SpokenLanguage extends BaseEntity {

    @Column(name = "iso_6391", nullable = false, length = 10, unique = true)
    private String iso6391;

    @Column(nullable = false, length = 255)
    private String englishName;

    @Column(length = 255)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpokenLanguage that) || getId() == null) return false;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SpokenLanguage{" +
                "id=" + getId() +
                ", iso6391='" + iso6391 + '\'' +
                '}';
    }
}
