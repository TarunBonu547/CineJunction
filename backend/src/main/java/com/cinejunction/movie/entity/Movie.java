package com.cinejunction.movie.entity;

import com.cinejunction.cast.entity.CastMember;
import com.cinejunction.common.entity.BaseEntity;
import com.cinejunction.genre.entity.Genre;
import com.cinejunction.movie.enums.MovieStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Movie extends BaseEntity {

    @NotBlank
    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "release_date")
    private java.time.LocalDate releaseDate;

    @Positive
    @Column(nullable = false)
    private Integer runtime;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String language;

    @Column(length = 500)
    private String posterUrl;

    @Column(length = 500)
    private String backdropUrl;

    @Column(length = 500)
    private String trailerUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MovieStatus status;

    @Column(nullable = false)
    @Builder.Default
    private boolean adult = false;

    @Column(nullable = false)
    @Builder.Default
    private Long budget = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long revenue = 0L;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Integer voteCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal popularity = BigDecimal.ZERO;

    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_production_companies",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "production_company_id")
    )
    @Builder.Default
    private Set<ProductionCompany> productionCompanies = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_production_countries",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "production_country_id")
    )
    @Builder.Default
    private Set<ProductionCountry> productionCountries = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_spoken_languages",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "spoken_language_id")
    )
    @Builder.Default
    private Set<SpokenLanguage> spokenLanguages = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "movie_keywords",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    @Builder.Default
    private Set<Keyword> keywords = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<CastMember> castMembers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return getId() != null && getId().equals(movie.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                '}';
    }
}
