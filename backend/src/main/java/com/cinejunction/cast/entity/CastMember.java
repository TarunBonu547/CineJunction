package com.cinejunction.cast.entity;

import com.cinejunction.common.entity.BaseEntity;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.enums.CastRole;
import com.cinejunction.person.entity.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "cast_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CastMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String characterName;

    @Column(nullable = false)
    private Integer castOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CastRole role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CastMember castMember)) return false;
        return getId() != null && getId().equals(castMember.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CastMember{" +
                "id=" + getId() +
                ", characterName='" + characterName + '\'' +
                '}';
    }
}
