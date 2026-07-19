package com.cinejunction.movieperson.entity;

import com.cinejunction.common.entity.BaseEntity;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movieperson.enums.RoleType;
import com.cinejunction.person.entity.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Check;

@Entity
@Table(
        name = "movie_people",
        uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "person_id", "role_type"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Check(constraints = "billing_order >= 0")
public class MoviePerson extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoleType roleType;

    @Size(max = 200)
    @Column(length = 200)
    private String characterName;

    @Size(max = 200)
    @Column(length = 200)
    private String creditedAs;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer billingOrder = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoviePerson that) || getId() == null) return false;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "MoviePerson{" +
                "id=" + getId() +
                ", roleType=" + roleType +
                '}';
    }
}
