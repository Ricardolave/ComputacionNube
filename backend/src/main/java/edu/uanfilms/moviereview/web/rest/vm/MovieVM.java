package edu.uanfilms.moviereview.web.rest.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uanfilms.moviereview.domain.Actor;
import edu.uanfilms.moviereview.domain.Genre;

import java.util.HashSet;
import java.util.Set;

public class MovieVM {
    private String name;
    private Integer year;
    private String director;
    @JsonProperty("genero")
    private Set<Genre> genres = new HashSet<>();
    @JsonProperty("elenco")
    private Set<Actor> actors = new HashSet<>();
}
