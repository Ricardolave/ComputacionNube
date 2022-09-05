package edu.uanfilms.moviereview.web.rest.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uanfilms.moviereview.domain.Actor;
import edu.uanfilms.moviereview.domain.Genre;
import edu.uanfilms.moviereview.domain.Movie;

import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieVM {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("year")
    private Integer year;
    @JsonProperty("director")
    private String director;
    @JsonProperty("genero")
    private String genres;

    @JsonProperty("sinopsis")
    private String synopsis;
    @JsonProperty("elenco")
    private Set<Actor> actors = new HashSet<>();

    @JsonProperty("reseñas")
    private Set<ReviewVM> reviews = new HashSet<>();

    public MovieVM() {
    }

    public MovieVM(Movie movie) {
        this.id = movie.getId();
        this.name = movie.getName();
        this.director = movie.getDirector();
        this.year = movie.getYear().atZone(ZoneOffset.UTC).getYear();
        Set<Genre> genres = movie.getGenres();
        if (!genres.isEmpty()) {
            this.genres = genres.stream().findFirst().get().getName();
        }
        this.synopsis = movie.getSynopsis();
        this.reviews = movie.getReviews().stream().map((mov) -> new ReviewVM(mov)).collect(Collectors.toSet());
    }

    public Movie toMovie() {
        Movie convertedMovie = new Movie(this.id);
        convertedMovie.setName(this.name);
        convertedMovie.setDirector(this.director);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, this.year);
        convertedMovie.year(calendar.toInstant());
        convertedMovie.setSynopsis(this.synopsis);
        return convertedMovie;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public Set<Actor> getActors() {
        return actors;
    }

    public void setActors(Set<Actor> actors) {
        this.actors = actors;
    }

}
