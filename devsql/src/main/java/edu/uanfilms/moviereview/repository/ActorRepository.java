package edu.uanfilms.moviereview.repository;

import edu.uanfilms.moviereview.domain.Actor;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Actor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {}
