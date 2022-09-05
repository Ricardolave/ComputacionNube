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
    private Integer movie;

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

    public Integer getMovie() {
        return movie;
    }

    public void setMovie(Integer movie) {
        this.movie = movie;
    }

    public Review toReview() {
        Review review = new Review();
        review.setScore(this.getScore());
        review.setComment(this.getComment());
        review.setDate(Instant.now());
        review.setMovie(new Movie(Long.valueOf(this.movie)));
        return review;
    }

    public ReviewVM(Review review) {
        this.score = review.getScore();
        this.movie = Math.toIntExact(review.getMovie().getId());
        this.comment = review.getComment();
    }

    public ReviewVM() {
    }
}
