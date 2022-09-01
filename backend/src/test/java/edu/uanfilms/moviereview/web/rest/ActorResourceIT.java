package edu.uanfilms.moviereview.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.uanfilms.moviereview.IntegrationTest;
import edu.uanfilms.moviereview.domain.Actor;
import edu.uanfilms.moviereview.repository.ActorRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ActorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restActorMockMvc;

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

    @BeforeEach
    public void initTest() {
        actor = createEntity(em);
    }

    @Test
    @Transactional
    void createActor() throws Exception {
        int databaseSizeBeforeCreate = actorRepository.findAll().size();
        // Create the Actor
        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isCreated());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate + 1);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testActor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testActor.getBirthDay()).isEqualTo(DEFAULT_BIRTH_DAY);
    }

    @Test
    @Transactional
    void createActorWithExistingId() throws Exception {
        // Create the Actor with an existing ID
        actor.setId(1L);

        int databaseSizeBeforeCreate = actorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllActors() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList
        restActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].birthDay").value(hasItem(DEFAULT_BIRTH_DAY.toString())));
    }

    @Test
    @Transactional
    void getActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get the actor
        restActorMockMvc
            .perform(get(ENTITY_API_URL_ID, actor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(actor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.birthDay").value(DEFAULT_BIRTH_DAY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingActor() throws Exception {
        // Get the actor
        restActorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor
        Actor updatedActor = actorRepository.findById(actor.getId()).get();
        // Disconnect from session so that the updates on updatedActor are not directly saved in db
        em.detach(updatedActor);
        updatedActor.name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).birthDay(UPDATED_BIRTH_DAY);

        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedActor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
    }

    @Test
    @Transactional
    void putNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setId(actor.getId());

        partialUpdatedActor.name(UPDATED_NAME).birthDay(UPDATED_BIRTH_DAY);

        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testActor.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
    }

    @Test
    @Transactional
    void fullUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setId(actor.getId());

        partialUpdatedActor.name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).birthDay(UPDATED_BIRTH_DAY);

        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getBirthDay()).isEqualTo(UPDATED_BIRTH_DAY);
    }

    @Test
    @Transactional
    void patchNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, actor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeDelete = actorRepository.findAll().size();

        // Delete the actor
        restActorMockMvc
            .perform(delete(ENTITY_API_URL_ID, actor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
