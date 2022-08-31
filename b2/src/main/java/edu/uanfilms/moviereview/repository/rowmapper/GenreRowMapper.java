package edu.uanfilms.moviereview.repository.rowmapper;

import edu.uanfilms.moviereview.domain.Genre;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Genre}, with proper type conversions.
 */
@Service
public class GenreRowMapper implements BiFunction<Row, String, Genre> {

    private final ColumnConverter converter;

    public GenreRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Genre} stored in the database.
     */
    @Override
    public Genre apply(Row row, String prefix) {
        Genre entity = new Genre();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
