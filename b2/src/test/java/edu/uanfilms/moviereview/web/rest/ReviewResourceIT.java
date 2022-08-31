package edu.uanfilms.moviereview.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import edu.uanfilms.moviereview.IntegrationTest;
import edu.uanfilms.moviereview.domain.Review;
import edu.uanfilms.moviereview.repository.EntityManager;
import edu.uanfilms.moviereview.repository.ReviewRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ReviewResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ReviewResourceIT {

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Review review;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Review createEntity(EntityManager em) {
        Review review = new Review().score(DEFAULT_SCORE).date(DEFAULT_DATE).comment(DEFAULT_COMMENT);
        return review;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Review createUpdatedEntity(EntityManager em) {
        Review review = new Review().score(UPDATED_SCORE).date(UPDATED_DATE).comment(UPDATED_COMMENT);
        return review;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Review.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        review = createEntity(em);
    }

    @Test
    void createReview() throws Exception {
        int databaseSizeBeforeCreate = reviewRepository.findAll().collectList().block().size();
        // Create the Review
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(review))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeCreate + 1);
        Review testReview = reviewList.get(reviewList.size() - 1);
        assertThat(testReview.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testReview.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testReview.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    void createReviewWithExistingId() throws Exception {
        // Create the Review with an existing ID
        review.setId(1L);

        int databaseSizeBeforeCreate = reviewRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(review))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllReviewsAsStream() {
        // Initialize the database
        reviewRepository.save(review).block();

        List<Review> reviewList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Review.class)
            .getResponseBody()
            .filter(review::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(reviewList).isNotNull();
        assertThat(reviewList).hasSize(1);
        Review testReview = reviewList.get(0);
        assertThat(testReview.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testReview.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testReview.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    void getAllReviews() {
        // Initialize the database
        reviewRepository.save(review).block();

        // Get all the reviewList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(review.getId().intValue()))
            .jsonPath("$.[*].score")
            .value(hasItem(DEFAULT_SCORE))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].comment")
            .value(hasItem(DEFAULT_COMMENT));
    }

    @Test
    void getReview() {
        // Initialize the database
        reviewRepository.save(review).block();

        // Get the review
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, review.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(review.getId().intValue()))
            .jsonPath("$.score")
            .value(is(DEFAULT_SCORE))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.comment")
            .value(is(DEFAULT_COMMENT));
    }

    @Test
    void getNonExistingReview() {
        // Get the review
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewReview() throws Exception {
        // Initialize the database
        reviewRepository.save(review).block();

        int databaseSizeBeforeUpdate = reviewRepository.findAll().collectList().block().size();

        // Update the review
        Review updatedReview = reviewRepository.findById(review.getId()).block();
        updatedReview.score(UPDATED_SCORE).date(UPDATED_DATE).comment(UPDATED_COMMENT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedReview.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeUpdate);
        Review testReview = reviewList.get(reviewList.size() - 1);
        assertThat(testReview.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testReview.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testReview.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    void putNonExistingReview() throws Exception {
        int databaseSizeBeforeUpdate = reviewRepository.findAll().collectList().block().size();
        review.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, review.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(review))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchReview() throws Exception {
        int databaseSizeBeforeUpdate = reviewRepository.findAll().collectList().block().size();
        review.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(review))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamReview() throws Exception {
        int databaseSizeBeforeUpdate = reviewRepository.findAll().collectList().block().size();
        review.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(review))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        reviewRepository.save(review).block();

        int databaseSizeBeforeUpdate = reviewRepository.findAll().collectList().block().size();

        // Update the review using partial update
        Review partialUpdatedReview = new Review();
        partialUpdatedReview.setId(review.getId());

        partialUpdatedReview.comment(UPDATED_COMMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeUpdate);
        Review testReview = reviewList.get(reviewList.size() - 1);
        assertThat(testReview.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testReview.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testReview.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    void fullUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        reviewRepository.save(review).block();

        int databaseSizeBeforeUpdate = reviewRepository.findAll().collectList().block().size();

        // Update the review using partial update
        Review partialUpdatedReview = new Review();
        partialUpdatedReview.setId(review.getId());

        partialUpdatedReview.score(UPDATED_SCORE).date(UPDATED_DATE).comment(UPDATED_COMMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeUpdate);
        Review testReview = reviewList.get(reviewList.size() - 1);
        assertThat(testReview.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testReview.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testReview.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    void patchNonExistingReview() throws Exception {
        int databaseSizeBeforeUpdate = reviewRepository.findAll().collectList().block().size();
        review.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, review.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(review))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchReview() throws Exception {
        int databaseSizeBeforeUpdate = reviewRepository.findAll().collectList().block().size();
        review.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(review))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamReview() throws Exception {
        int databaseSizeBeforeUpdate = reviewRepository.findAll().collectList().block().size();
        review.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(review))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Review in the database
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteReview() {
        // Initialize the database
        reviewRepository.save(review).block();

        int databaseSizeBeforeDelete = reviewRepository.findAll().collectList().block().size();

        // Delete the review
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, review.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Review> reviewList = reviewRepository.findAll().collectList().block();
        assertThat(reviewList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
