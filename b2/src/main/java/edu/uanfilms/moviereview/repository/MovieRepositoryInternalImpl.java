package edu.uanfilms.moviereview.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import edu.uanfilms.moviereview.domain.Actor;
import edu.uanfilms.moviereview.domain.Genre;
import edu.uanfilms.moviereview.domain.Movie;
import edu.uanfilms.moviereview.repository.rowmapper.MovieRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Movie entity.
 */
@SuppressWarnings("unused")
class MovieRepositoryInternalImpl extends SimpleR2dbcRepository<Movie, Long> implements MovieRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MovieRowMapper movieMapper;

    private static final Table entityTable = Table.aliased("movie", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable genreLink = new EntityManager.LinkTable("rel_movie__genre", "movie_id", "genre_id");
    private static final EntityManager.LinkTable actorLink = new EntityManager.LinkTable("rel_movie__actor", "movie_id", "actor_id");

    public MovieRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MovieRowMapper movieMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Movie.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.movieMapper = movieMapper;
    }

    @Override
    public Flux<Movie> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Movie> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MovieSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Movie.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Movie> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Movie> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Movie> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Movie> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Movie> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Movie process(Row row, RowMetadata metadata) {
        Movie entity = movieMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Movie> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Movie> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(genreLink, entity.getId(), entity.getGenres().stream().map(Genre::getId)).then();
        result = result.and(entityManager.updateLinkTable(actorLink, entity.getId(), entity.getActors().stream().map(Actor::getId)));
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(genreLink, entityId).and(entityManager.deleteFromLinkTable(actorLink, entityId));
    }
}
