package com.cinejunction.review.entity;

import com.cinejunction.common.entity.BaseEntity;
import com.cinejunction.entity.User;
import com.cinejunction.movie.entity.Movie;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "movie_reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovieReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(min = 5, max = 150, message = "Review title must be between 5 and 150 characters")
    @Column(nullable = false, length = 150)
    private String title;

    @Size(min = 20, max = 5000, message = "Review text must be between 20 and 5000 characters")
    @Column(nullable = false, length = 5000)
    private String reviewText;

    @Column(nullable = false)
    private Boolean containsSpoilers = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovieReview that) || getId() == null) return false;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "MovieReview{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                '}';
    }
}
