package edu.uanfilms.moviereview.web.rest;

import edu.uanfilms.moviereview.domain.Actor;
import edu.uanfilms.moviereview.repository.ActorRepository;
import edu.uanfilms.moviereview.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link edu.uanfilms.moviereview.domain.Actor}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ActorResource {

    private final Logger log = LoggerFactory.getLogger(ActorResource.class);

    private static final String ENTITY_NAME = "uanfilmsActor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActorRepository actorRepository;

    public ActorResource(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    /**
     * {@code POST  /actors} : Create a new actor.
     *
     * @param actor the actor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new actor, or with status {@code 400 (Bad Request)} if the actor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/actors")
    public ResponseEntity<Actor> createActor(@RequestBody Actor actor) throws URISyntaxException {
        log.debug("REST request to save Actor : {}", actor);
        if (actor.getId() != null) {
            throw new BadRequestAlertException("A new actor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Actor result = actorRepository.save(actor);
        return ResponseEntity
            .created(new URI("/api/actors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /actors/:id} : Updates an existing actor.
     *
     * @param id the id of the actor to save.
     * @param actor the actor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actor,
     * or with status {@code 400 (Bad Request)} if the actor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the actor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/actors/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable(value = "id", required = false) final Long id, @RequestBody Actor actor)
        throws URISyntaxException {
        log.debug("REST request to update Actor : {}, {}", id, actor);
        if (actor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Actor result = actorRepository.save(actor);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, actor.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /actors/:id} : Partial updates given fields of an existing actor, field will ignore if it is null
     *
     * @param id the id of the actor to save.
     * @param actor the actor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actor,
     * or with status {@code 400 (Bad Request)} if the actor is not valid,
     * or with status {@code 404 (Not Found)} if the actor is not found,
     * or with status {@code 500 (Internal Server Error)} if the actor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/actors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Actor> partialUpdateActor(@PathVariable(value = "id", required = false) final Long id, @RequestBody Actor actor)
        throws URISyntaxException {
        log.debug("REST request to partial update Actor partially : {}, {}", id, actor);
        if (actor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Actor> result = actorRepository
            .findById(actor.getId())
            .map(existingActor -> {
                if (actor.getName() != null) {
                    existingActor.setName(actor.getName());
                }
                if (actor.getLastName() != null) {
                    existingActor.setLastName(actor.getLastName());
                }
                if (actor.getBirthDay() != null) {
                    existingActor.setBirthDay(actor.getBirthDay());
                }

                return existingActor;
            })
            .map(actorRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, actor.getId().toString())
        );
    }

    /**
     * {@code GET  /actors} : get all the actors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actors in body.
     */
    @GetMapping("/actors")
    public List<Actor> getAllActors() {
        log.debug("REST request to get all Actors");
        return actorRepository.findAll();
    }

    /**
     * {@code GET  /actors/:id} : get the "id" actor.
     *
     * @param id the id of the actor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/actors/{id}")
    public ResponseEntity<Actor> getActor(@PathVariable Long id) {
        log.debug("REST request to get Actor : {}", id);
        Optional<Actor> actor = actorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(actor);
    }

    /**
     * {@code DELETE  /actors/:id} : delete the "id" actor.
     *
     * @param id the id of the actor to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/actors/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        log.debug("REST request to delete Actor : {}", id);
        actorRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
