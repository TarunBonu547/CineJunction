package com.cinejunction.movie.entity;

import com.cinejunction.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "collections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Collection extends BaseEntity {

    @Column(name = "tmdb_id", nullable = false, unique = true)
    private Long tmdbId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String posterPath;

    @Column(length = 500)
    private String backdropPath;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Collection that) || getId() == null) return false;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Collection{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
