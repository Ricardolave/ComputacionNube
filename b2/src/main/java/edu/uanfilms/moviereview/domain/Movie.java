package edu.uanfilms.moviereview.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Movie.
 */
@Table("movie")
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("hash")
    private String hash;

    @Column("year")
    private Instant year;

    @Column("director")
    private String director;

    @Column("synopsis")
    private String synopsis;

    @Transient
    @JsonIgnoreProperties(value = { "movie" }, allowSetters = true)
    private Set<Review> reviews = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "movies" }, allowSetters = true)
    private Set<Genre> genres = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "casts" }, allowSetters = true)
    private Set<Actor> actors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Movie id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Movie name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return this.hash;
    }

    public Movie hash(String hash) {
        this.setHash(hash);
        return this;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Instant getYear() {
        return this.year;
    }

    public Movie year(Instant year) {
        this.setYear(year);
        return this;
    }

    public void setYear(Instant year) {
        this.year = year;
    }

    public String getDirector() {
        return this.director;
    }

    public Movie director(String director) {
        this.setDirector(director);
        return this;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public Movie synopsis(String synopsis) {
        this.setSynopsis(synopsis);
        return this;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Set<Review> getReviews() {
        return this.reviews;
    }

    public void setReviews(Set<Review> reviews) {
        if (this.reviews != null) {
            this.reviews.forEach(i -> i.setMovie(null));
        }
        if (reviews != null) {
            reviews.forEach(i -> i.setMovie(this));
        }
        this.reviews = reviews;
    }

    public Movie reviews(Set<Review> reviews) {
        this.setReviews(reviews);
        return this;
    }

    public Movie addReview(Review review) {
        this.reviews.add(review);
        review.setMovie(this);
        return this;
    }

    public Movie removeReview(Review review) {
        this.reviews.remove(review);
        review.setMovie(null);
        return this;
    }

    public Set<Genre> getGenres() {
        return this.genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Movie genres(Set<Genre> genres) {
        this.setGenres(genres);
        return this;
    }

    public Movie addGenre(Genre genre) {
        this.genres.add(genre);
        genre.getMovies().add(this);
        return this;
    }

    public Movie removeGenre(Genre genre) {
        this.genres.remove(genre);
        genre.getMovies().remove(this);
        return this;
    }

    public Set<Actor> getActors() {
        return this.actors;
    }

    public void setActors(Set<Actor> actors) {
        this.actors = actors;
    }

    public Movie actors(Set<Actor> actors) {
        this.setActors(actors);
        return this;
    }

    public Movie addActor(Actor actor) {
        this.actors.add(actor);
        actor.getCasts().add(this);
        return this;
    }

    public Movie removeActor(Actor actor) {
        this.actors.remove(actor);
        actor.getCasts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Movie)) {
            return false;
        }
        return id != null && id.equals(((Movie) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Movie{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", hash='" + getHash() + "'" +
            ", year='" + getYear() + "'" +
            ", director='" + getDirector() + "'" +
            ", synopsis='" + getSynopsis() + "'" +
            "}";
    }
}
