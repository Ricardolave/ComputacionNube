package edu.uanfilms.moviereview.repository;

import edu.uanfilms.moviereview.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Review entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReviewRepository extends ReactiveCrudRepository<Review, Long>, ReviewRepositoryInternal {
    @Query("SELECT * FROM review entity WHERE entity.movie_id = :id")
    Flux<Review> findByMovie(Long id);

    @Query("SELECT * FROM review entity WHERE entity.movie_id IS NULL")
    Flux<Review> findAllWhereMovieIsNull();

    @Override
    <S extends Review> Mono<S> save(S entity);

    @Override
    Flux<Review> findAll();

    @Override
    Mono<Review> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ReviewRepositoryInternal {
    <S extends Review> Mono<S> save(S entity);

    Flux<Review> findAllBy(Pageable pageable);

    Flux<Review> findAll();

    Mono<Review> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Review> findAllBy(Pageable pageable, Criteria criteria);

}
