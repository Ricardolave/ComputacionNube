package edu.uanfilms.moviereview.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import edu.uanfilms.moviereview.IntegrationTest;
import edu.uanfilms.moviereview.domain.Movie;
import edu.uanfilms.moviereview.repository.EntityManager;
import edu.uanfilms.moviereview.repository.MovieRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link MovieResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MovieResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HASH = "AAAAAAAAAA";
    private static final String UPDATED_HASH = "BBBBBBBBBB";

    private static final Instant DEFAULT_YEAR = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_YEAR = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DIRECTOR = "AAAAAAAAAA";
    private static final String UPDATED_DIRECTOR = "BBBBBBBBBB";

    private static final String DEFAULT_SYNOPSIS = "AAAAAAAAAA";
    private static final String UPDATED_SYNOPSIS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/movies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MovieRepository movieRepository;

    @Mock
    private MovieRepository movieRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Movie movie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movie createEntity(EntityManager em) {
        Movie movie = new Movie()
            .name(DEFAULT_NAME)
            .hash(DEFAULT_HASH)
            .year(DEFAULT_YEAR)
            .director(DEFAULT_DIRECTOR)
            .synopsis(DEFAULT_SYNOPSIS);
        return movie;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Movie createUpdatedEntity(EntityManager em) {
        Movie movie = new Movie()
            .name(UPDATED_NAME)
            .hash(UPDATED_HASH)
            .year(UPDATED_YEAR)
            .director(UPDATED_DIRECTOR)
            .synopsis(UPDATED_SYNOPSIS);
        return movie;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_movie__genre").block();
            em.deleteAll("rel_movie__actor").block();
            em.deleteAll(Movie.class).block();
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
        movie = createEntity(em);
    }

    @Test
    void createMovie() throws Exception {
        int databaseSizeBeforeCreate = movieRepository.findAll().collectList().block().size();
        // Create the Movie
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeCreate + 1);
        Movie testMovie = movieList.get(movieList.size() - 1);
        assertThat(testMovie.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMovie.getHash()).isEqualTo(DEFAULT_HASH);
        assertThat(testMovie.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testMovie.getDirector()).isEqualTo(DEFAULT_DIRECTOR);
        assertThat(testMovie.getSynopsis()).isEqualTo(DEFAULT_SYNOPSIS);
    }

    @Test
    void createMovieWithExistingId() throws Exception {
        // Create the Movie with an existing ID
        movie.setId(1L);

        int databaseSizeBeforeCreate = movieRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = movieRepository.findAll().collectList().block().size();
        // set the field null
        movie.setName(null);

        // Create the Movie, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkHashIsRequired() throws Exception {
        int databaseSizeBeforeTest = movieRepository.findAll().collectList().block().size();
        // set the field null
        movie.setHash(null);

        // Create the Movie, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllMoviesAsStream() {
        // Initialize the database
        movieRepository.save(movie).block();

        List<Movie> movieList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Movie.class)
            .getResponseBody()
            .filter(movie::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(movieList).isNotNull();
        assertThat(movieList).hasSize(1);
        Movie testMovie = movieList.get(0);
        assertThat(testMovie.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMovie.getHash()).isEqualTo(DEFAULT_HASH);
        assertThat(testMovie.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testMovie.getDirector()).isEqualTo(DEFAULT_DIRECTOR);
        assertThat(testMovie.getSynopsis()).isEqualTo(DEFAULT_SYNOPSIS);
    }

    @Test
    void getAllMovies() {
        // Initialize the database
        movieRepository.save(movie).block();

        // Get all the movieList
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
            .value(hasItem(movie.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].hash")
            .value(hasItem(DEFAULT_HASH))
            .jsonPath("$.[*].year")
            .value(hasItem(DEFAULT_YEAR.toString()))
            .jsonPath("$.[*].director")
            .value(hasItem(DEFAULT_DIRECTOR))
            .jsonPath("$.[*].synopsis")
            .value(hasItem(DEFAULT_SYNOPSIS));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMoviesWithEagerRelationshipsIsEnabled() {
        when(movieRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(movieRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMoviesWithEagerRelationshipsIsNotEnabled() {
        when(movieRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(movieRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMovie() {
        // Initialize the database
        movieRepository.save(movie).block();

        // Get the movie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, movie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(movie.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.hash")
            .value(is(DEFAULT_HASH))
            .jsonPath("$.year")
            .value(is(DEFAULT_YEAR.toString()))
            .jsonPath("$.director")
            .value(is(DEFAULT_DIRECTOR))
            .jsonPath("$.synopsis")
            .value(is(DEFAULT_SYNOPSIS));
    }

    @Test
    void getNonExistingMovie() {
        // Get the movie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewMovie() throws Exception {
        // Initialize the database
        movieRepository.save(movie).block();

        int databaseSizeBeforeUpdate = movieRepository.findAll().collectList().block().size();

        // Update the movie
        Movie updatedMovie = movieRepository.findById(movie.getId()).block();
        updatedMovie.name(UPDATED_NAME).hash(UPDATED_HASH).year(UPDATED_YEAR).director(UPDATED_DIRECTOR).synopsis(UPDATED_SYNOPSIS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMovie.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedMovie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
        Movie testMovie = movieList.get(movieList.size() - 1);
        assertThat(testMovie.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMovie.getHash()).isEqualTo(UPDATED_HASH);
        assertThat(testMovie.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testMovie.getDirector()).isEqualTo(UPDATED_DIRECTOR);
        assertThat(testMovie.getSynopsis()).isEqualTo(UPDATED_SYNOPSIS);
    }

    @Test
    void putNonExistingMovie() throws Exception {
        int databaseSizeBeforeUpdate = movieRepository.findAll().collectList().block().size();
        movie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, movie.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMovie() throws Exception {
        int databaseSizeBeforeUpdate = movieRepository.findAll().collectList().block().size();
        movie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMovie() throws Exception {
        int databaseSizeBeforeUpdate = movieRepository.findAll().collectList().block().size();
        movie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMovieWithPatch() throws Exception {
        // Initialize the database
        movieRepository.save(movie).block();

        int databaseSizeBeforeUpdate = movieRepository.findAll().collectList().block().size();

        // Update the movie using partial update
        Movie partialUpdatedMovie = new Movie();
        partialUpdatedMovie.setId(movie.getId());

        partialUpdatedMovie.year(UPDATED_YEAR).director(UPDATED_DIRECTOR).synopsis(UPDATED_SYNOPSIS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMovie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMovie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
        Movie testMovie = movieList.get(movieList.size() - 1);
        assertThat(testMovie.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMovie.getHash()).isEqualTo(DEFAULT_HASH);
        assertThat(testMovie.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testMovie.getDirector()).isEqualTo(UPDATED_DIRECTOR);
        assertThat(testMovie.getSynopsis()).isEqualTo(UPDATED_SYNOPSIS);
    }

    @Test
    void fullUpdateMovieWithPatch() throws Exception {
        // Initialize the database
        movieRepository.save(movie).block();

        int databaseSizeBeforeUpdate = movieRepository.findAll().collectList().block().size();

        // Update the movie using partial update
        Movie partialUpdatedMovie = new Movie();
        partialUpdatedMovie.setId(movie.getId());

        partialUpdatedMovie.name(UPDATED_NAME).hash(UPDATED_HASH).year(UPDATED_YEAR).director(UPDATED_DIRECTOR).synopsis(UPDATED_SYNOPSIS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMovie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMovie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
        Movie testMovie = movieList.get(movieList.size() - 1);
        assertThat(testMovie.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMovie.getHash()).isEqualTo(UPDATED_HASH);
        assertThat(testMovie.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testMovie.getDirector()).isEqualTo(UPDATED_DIRECTOR);
        assertThat(testMovie.getSynopsis()).isEqualTo(UPDATED_SYNOPSIS);
    }

    @Test
    void patchNonExistingMovie() throws Exception {
        int databaseSizeBeforeUpdate = movieRepository.findAll().collectList().block().size();
        movie.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, movie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMovie() throws Exception {
        int databaseSizeBeforeUpdate = movieRepository.findAll().collectList().block().size();
        movie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMovie() throws Exception {
        int databaseSizeBeforeUpdate = movieRepository.findAll().collectList().block().size();
        movie.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(movie))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Movie in the database
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMovie() {
        // Initialize the database
        movieRepository.save(movie).block();

        int databaseSizeBeforeDelete = movieRepository.findAll().collectList().block().size();

        // Delete the movie
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, movie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Movie> movieList = movieRepository.findAll().collectList().block();
        assertThat(movieList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
