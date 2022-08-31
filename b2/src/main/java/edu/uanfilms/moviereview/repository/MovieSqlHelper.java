package edu.uanfilms.moviereview.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MovieSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("hash", table, columnPrefix + "_hash"));
        columns.add(Column.aliased("year", table, columnPrefix + "_year"));
        columns.add(Column.aliased("director", table, columnPrefix + "_director"));
        columns.add(Column.aliased("synopsis", table, columnPrefix + "_synopsis"));

        return columns;
    }
}
