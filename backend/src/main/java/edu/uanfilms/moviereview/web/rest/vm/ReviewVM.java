package edu.uanfilms.moviereview.web.rest.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uanfilms.moviereview.domain.Movie;
import edu.uanfilms.moviereview.domain.Review;

import java.time.Instant;

public class ReviewVM {

    @JsonProperty("calificacion")
    private Integer score;

    @JsonProperty("reseña")
    private String comment;

    @JsonProperty("pelicula")
    private Movie movie;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Review toReview() {
        Review review = new Review();
        review.setScore(this.getScore());
        review.setComment(this.getComment());
        review.setDate(Instant.now());
        review.setMovie(this.getMovie());
        return review;
    }

}
