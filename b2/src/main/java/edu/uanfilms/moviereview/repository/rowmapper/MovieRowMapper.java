package edu.uanfilms.moviereview.repository.rowmapper;

import edu.uanfilms.moviereview.domain.Movie;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Movie}, with proper type conversions.
 */
@Service
public class MovieRowMapper implements BiFunction<Row, String, Movie> {

    private final ColumnConverter converter;

    public MovieRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Movie} stored in the database.
     */
    @Override
    public Movie apply(Row row, String prefix) {
        Movie entity = new Movie();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setHash(converter.fromRow(row, prefix + "_hash", String.class));
        entity.setYear(converter.fromRow(row, prefix + "_year", Instant.class));
        entity.setDirector(converter.fromRow(row, prefix + "_director", String.class));
        entity.setSynopsis(converter.fromRow(row, prefix + "_synopsis", String.class));
        return entity;
    }
}
