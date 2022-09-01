package edu.uanfilms.moviereview.repository;

import edu.uanfilms.moviereview.domain.Review;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the Review entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional
public interface ReviewRepository extends JpaRepository<Review, Long> {}
