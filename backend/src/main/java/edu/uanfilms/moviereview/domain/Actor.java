package edu.uanfilms.moviereview.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Actor.
 */
@Entity
@Table(name = "actor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Actor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_day")
    private Instant birthDay;

    @ManyToMany(mappedBy = "actors")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "reviews", "genres", "actors" }, allowSetters = true)
    private Set<Movie> casts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Actor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Actor name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Actor lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Instant getBirthDay() {
        return this.birthDay;
    }

    public Actor birthDay(Instant birthDay) {
        this.setBirthDay(birthDay);
        return this;
    }

    public void setBirthDay(Instant birthDay) {
        this.birthDay = birthDay;
    }

    public Set<Movie> getCasts() {
        return this.casts;
    }

    public void setCasts(Set<Movie> movies) {
        if (this.casts != null) {
            this.casts.forEach(i -> i.removeActor(this));
        }
        if (movies != null) {
            movies.forEach(i -> i.addActor(this));
        }
        this.casts = movies;
    }

    public Actor casts(Set<Movie> movies) {
        this.setCasts(movies);
        return this;
    }

    public Actor addCast(Movie movie) {
        this.casts.add(movie);
        movie.getActors().add(this);
        return this;
    }

    public Actor removeCast(Movie movie) {
        this.casts.remove(movie);
        movie.getActors().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Actor)) {
            return false;
        }
        return id != null && id.equals(((Actor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Actor{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", birthDay='" + getBirthDay() + "'" +
            "}";
    }
}
