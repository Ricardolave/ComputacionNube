package edu.uanfilms.moviereview.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import edu.uanfilms.moviereview.domain.Review;
import edu.uanfilms.moviereview.repository.rowmapper.MovieRowMapper;
import edu.uanfilms.moviereview.repository.rowmapper.ReviewRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Review entity.
 */
@SuppressWarnings("unused")
class ReviewRepositoryInternalImpl extends SimpleR2dbcRepository<Review, Long> implements ReviewRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final MovieRowMapper movieMapper;
    private final ReviewRowMapper reviewMapper;

    private static final Table entityTable = Table.aliased("review", EntityManager.ENTITY_ALIAS);
    private static final Table movieTable = Table.aliased("movie", "movie");

    public ReviewRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        MovieRowMapper movieMapper,
        ReviewRowMapper reviewMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Review.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.movieMapper = movieMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public Flux<Review> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Review> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ReviewSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(MovieSqlHelper.getColumns(movieTable, "movie"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(movieTable)
            .on(Column.create("movie_id", entityTable))
            .equals(Column.create("id", movieTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Review.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Review> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Review> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Review process(Row row, RowMetadata metadata) {
        Review entity = reviewMapper.apply(row, "e");
        entity.setMovie(movieMapper.apply(row, "movie"));
        return entity;
    }

    @Override
    public <S extends Review> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
