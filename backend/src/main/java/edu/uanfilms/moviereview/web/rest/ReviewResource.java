package edu.uanfilms.moviereview.web.rest;

import edu.uanfilms.moviereview.domain.Review;
import edu.uanfilms.moviereview.repository.MovieRepository;
import edu.uanfilms.moviereview.repository.ReviewRepository;
import edu.uanfilms.moviereview.service.CorrectionService;
import edu.uanfilms.moviereview.web.rest.errors.BadRequestAlertException;
import edu.uanfilms.moviereview.web.rest.vm.ReviewVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link edu.uanfilms.moviereview.domain.Review}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ReviewResource {

    private final Logger log = LoggerFactory.getLogger(ReviewResource.class);
    private final CorrectionService correctionService;

    private final MovieRepository movieRepository;

    private static final String ENTITY_NAME = "uanfilmsReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReviewRepository reviewRepository;

    public ReviewResource(CorrectionService correctionService, MovieRepository movieRepository, ReviewRepository reviewRepository) {
        this.correctionService = correctionService;
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * {@code POST  /reviews} : Create a new review.
     *
     * @param review the review to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new review, or with status {@code 400 (Bad Request)} if the review has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reviews")
    public ResponseEntity<Review> createReview(@RequestBody ReviewVM review) throws URISyntaxException {
        log.debug("REST request to save Review : {}", review);
        if (review.getComment() == null)
            throw new BadRequestAlertException("A new review need comments", ENTITY_NAME, "idexists");
        Review reviewToSave = correctionService.correct(review.toReview());
        Review result = reviewRepository.save(reviewToSave);
        return ResponseEntity
            .created(new URI("/api/reviews/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reviews/:id} : Updates an existing review.
     *
     * @param id the id of the review to save.
     * @param review the review to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated review,
     * or with status {@code 400 (Bad Request)} if the review is not valid,
     * or with status {@code 500 (Internal Server Error)} if the review couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reviews/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable(value = "id", required = false) final Long id, @RequestBody Review review)
        throws URISyntaxException {
        log.debug("REST request to update Review : {}, {}", id, review);
        if (review.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, review.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Review result = reviewRepository.save(review);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, review.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /reviews/:id} : Partial updates given fields of an existing review, field will ignore if it is null
     *
     * @param id the id of the review to save.
     * @param review the review to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated review,
     * or with status {@code 400 (Bad Request)} if the review is not valid,
     * or with status {@code 404 (Not Found)} if the review is not found,
     * or with status {@code 500 (Internal Server Error)} if the review couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reviews/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Review> partialUpdateReview(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Review review
    ) throws URISyntaxException {
        log.debug("REST request to partial update Review partially : {}, {}", id, review);
        if (review.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, review.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Review> result = reviewRepository
            .findById(review.getId())
            .map(existingReview -> {
                if (review.getScore() != null) {
                    existingReview.setScore(review.getScore());
                }
                if (review.getDate() != null) {
                    existingReview.setDate(review.getDate());
                }
                if (review.getComment() != null) {
                    existingReview.setComment(review.getComment());
                }

                return existingReview;
            })
            .map(reviewRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, review.getId().toString())
        );
    }

    /**
     * {@code GET  /reviews} : get all the reviews.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reviews in body.
     */
    @GetMapping("/reviews")
    public List<Review> getAllReviews() {
        log.debug("REST request to get all Reviews");
        return reviewRepository.findAll();
    }

    /**
     * {@code GET  /reviews/:id} : get the "id" review.
     *
     * @param id the id of the review to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the review, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reviews/{id}")
    public ResponseEntity<Review> getReview(@PathVariable Long id) {
        log.debug("REST request to get Review : {}", id);
        Optional<Review> review = reviewRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(review);
    }

    /**
     * {@code DELETE  /reviews/:id} : delete the "id" review.
     *
     * @param id the id of the review to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        log.debug("REST request to delete Review : {}", id);
        reviewRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
