package edu.uanfilms.moviereview.repository;

import edu.uanfilms.moviereview.domain.Actor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Actor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActorRepository extends ReactiveCrudRepository<Actor, Long>, ActorRepositoryInternal {
    @Override
    <S extends Actor> Mono<S> save(S entity);

    @Override
    Flux<Actor> findAll();

    @Override
    Mono<Actor> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ActorRepositoryInternal {
    <S extends Actor> Mono<S> save(S entity);

    Flux<Actor> findAllBy(Pageable pageable);

    Flux<Actor> findAll();

    Mono<Actor> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Actor> findAllBy(Pageable pageable, Criteria criteria);

}
