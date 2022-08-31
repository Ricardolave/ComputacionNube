package edu.uanfilms.moviereview.repository.rowmapper;

import edu.uanfilms.moviereview.domain.Review;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Review}, with proper type conversions.
 */
@Service
public class ReviewRowMapper implements BiFunction<Row, String, Review> {

    private final ColumnConverter converter;

    public ReviewRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Review} stored in the database.
     */
    @Override
    public Review apply(Row row, String prefix) {
        Review entity = new Review();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setScore(converter.fromRow(row, prefix + "_score", Integer.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setComment(converter.fromRow(row, prefix + "_comment", String.class));
        entity.setMovieId(converter.fromRow(row, prefix + "_movie_id", Long.class));
        return entity;
    }
}
