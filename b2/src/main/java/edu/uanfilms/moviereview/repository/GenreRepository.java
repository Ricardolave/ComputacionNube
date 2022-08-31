package edu.uanfilms.moviereview.repository;

import edu.uanfilms.moviereview.domain.Genre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Genre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GenreRepository extends ReactiveCrudRepository<Genre, Long>, GenreRepositoryInternal {
    @Override
    <S extends Genre> Mono<S> save(S entity);

    @Override
    Flux<Genre> findAll();

    @Override
    Mono<Genre> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface GenreRepositoryInternal {
    <S extends Genre> Mono<S> save(S entity);

    Flux<Genre> findAllBy(Pageable pageable);

    Flux<Genre> findAll();

    Mono<Genre> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Genre> findAllBy(Pageable pageable, Criteria criteria);

}
