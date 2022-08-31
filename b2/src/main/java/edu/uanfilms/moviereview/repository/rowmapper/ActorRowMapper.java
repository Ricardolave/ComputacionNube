package edu.uanfilms.moviereview.repository.rowmapper;

import edu.uanfilms.moviereview.domain.Actor;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Actor}, with proper type conversions.
 */
@Service
public class ActorRowMapper implements BiFunction<Row, String, Actor> {

    private final ColumnConverter converter;

    public ActorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Actor} stored in the database.
     */
    @Override
    public Actor apply(Row row, String prefix) {
        Actor entity = new Actor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setBirthDay(converter.fromRow(row, prefix + "_birth_day", Instant.class));
        return entity;
    }
}
