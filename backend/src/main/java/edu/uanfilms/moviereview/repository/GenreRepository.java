package edu.uanfilms.moviereview.repository;

import edu.uanfilms.moviereview.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the Genre entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Genre findByName(String name);
}
