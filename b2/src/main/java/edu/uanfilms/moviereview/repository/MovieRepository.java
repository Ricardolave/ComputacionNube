package edu.uanfilms.moviereview.repository;

import edu.uanfilms.moviereview.domain.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Movie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MovieRepository extends ReactiveCrudRepository<Movie, Long>, MovieRepositoryInternal {
    @Override
    Mono<Movie> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Movie> findAllWithEagerRelationships();

    @Override
    Flux<Movie> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM movie entity JOIN rel_movie__genre joinTable ON entity.id = joinTable.genre_id WHERE joinTable.genre_id = :id"
    )
    Flux<Movie> findByGenre(Long id);

    @Query(
        "SELECT entity.* FROM movie entity JOIN rel_movie__actor joinTable ON entity.id = joinTable.actor_id WHERE joinTable.actor_id = :id"
    )
    Flux<Movie> findByActor(Long id);

    @Override
    <S extends Movie> Mono<S> save(S entity);

    @Override
    Flux<Movie> findAll();

    @Override
    Mono<Movie> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MovieRepositoryInternal {
    <S extends Movie> Mono<S> save(S entity);

    Flux<Movie> findAllBy(Pageable pageable);

    Flux<Movie> findAll();

    Mono<Movie> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Movie> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Movie> findOneWithEagerRelationships(Long id);

    Flux<Movie> findAllWithEagerRelationships();

    Flux<Movie> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
