package edu.uanfilms.moviereview.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import edu.uanfilms.moviereview.IntegrationTest;
import edu.uanfilms.moviereview.domain.Actor;
import edu.uanfilms.moviereview.repository.ActorRepository;
import edu.uanfilms.moviereview.repository.EntityManager;
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
 * Integration tests for the {@link ActorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ActorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_BIRTH_DAY = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BIRTH_DAY = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/actors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Actor actor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createEntity(EntityManager em) {
        Actor actor = new Actor().name(DEFAULT_NAME).lastName(DEFAULT_LAST_NAME).birthDay(DEFAULT_BIRTH_DAY);
        return actor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createUpdatedEntity(EntityManager em) {
        Actor actor = new Actor().name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).birthDay(UPDATED_BIRTH_DAY);
        return actor;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Actor.class).block();
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
        actor = createEntity(em);
    }

    @Test
    void createActor() throws Exception {
        int databaseSizeBeforeCreate = actorRepository.findAll().collectList().block().size();
        // Create the Actor
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(actor))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate + 1);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testActor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testActor.getBirthDay()).isEqualTo(DEFAULT_BIRTH_DAY);
    }

    @Test
    void createActorWithExistingId() throws Exception {
        // Create the Actor with an existing ID
        actor.setId(1L);

        int databaseSizeBeforeCreate = actorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(actor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllActorsAsStream() {
        // Initialize the database
        actorRepository.save(actor).block();

        List<Actor> actorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Actor.class)
            .getResponseBody()
            .filter(actor::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(actorList).isNotNull();
        assertThat(actorList).hasSize(1);
        Actor testActor = actorList.get(0);
        assertThat(testActor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testActor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testActor.getBirthDay()).isEqualTo(DEFAULT_BIRTH_DAY);
    }

    @Test
    void getAllActors() {
        // Initialize the database
        actorRepository.save(actor).block();

        // Get all the actorList
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
            .value(hasItem(actor.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].birthDay")
            .value(hasItem(DEFAULT_BIRTH_DAY.toString()));
    }

    @Test
    void getActor() {
        // Initialize the database
        actorRepository.save(actor).block();

        // Get the actor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, actor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(actor.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.birthDay")
            .value(is(DEFAULT_BIRTH_DAY.toString()));
    }

    @Test
    void getNonExistingActor() {
        // Get the actor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewActor() throws Exception {
        // Initialize the database
        actorRepository.save(actor).block();

        int databaseSizeBeforeUpdate = actorRepository.findAll().collectList().block().size();

        // Update the actor
        Actor updatedActor = actorRepository.findById(actor.getId()).block();
        updatedActor.name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).birthDay(UPDATED_BIRTH_DAY);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedActor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedActor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
    }

    @Test
    void putNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().collectList().block().size();
        actor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, actor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(actor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().collectList().block().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(actor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().collectList().block().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(actor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.save(actor).block();

        int databaseSizeBeforeUpdate = actorRepository.findAll().collectList().block().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setId(actor.getId());

        partialUpdatedActor.name(UPDATED_NAME).birthDay(UPDATED_BIRTH_DAY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedActor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testActor.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
    }

    @Test
    void fullUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.save(actor).block();

        int databaseSizeBeforeUpdate = actorRepository.findAll().collectList().block().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setId(actor.getId());

        partialUpdatedActor.name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).birthDay(UPDATED_BIRTH_DAY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedActor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
    }

    @Test
    void patchNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().collectList().block().size();
        actor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, actor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(actor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().collectList().block().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(actor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().collectList().block().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(actor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteActor() {
        // Initialize the database
        actorRepository.save(actor).block();

        int databaseSizeBeforeDelete = actorRepository.findAll().collectList().block().size();

        // Delete the actor
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, actor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Actor> actorList = actorRepository.findAll().collectList().block();
        assertThat(actorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
