package by.sakujj.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class SqlQueries {
    public static String getSelectById(String tableName, String idAttributeName) {
        return """
                SELECT *
                    FROM %s
                WHERE %s = ?
                """.formatted(tableName, idAttributeName);
    }

    public static String getSelectAll(String tableName) {
        return """
                SELECT *
                    FROM %s
                """.formatted(tableName);
    }

    public static String getUpdateById(String tableName, String idAttributeName,
                                       List<String> attributesToUpdate) {
        return """
                UPDATE %s
                %s
                WHERE %s = ?
                """.formatted(
                tableName,
                attributesToUpdate
                        .stream()
                        .map("\tSET %s = ?"::formatted)
                        .collect(Collectors.joining(",\n")),
                idAttributeName);

    }

    public static String getDeleteById(String tableName, String idAttributeName) {
        return """
                DELETE FROM %s
                WHERE %s = ?
                """.formatted(tableName, idAttributeName);
    }

    public static String getInsert(String tableName, List<String> attributes) {
        return """
                INSERT INTO %s
                    (%s)
                VALUES
                    (%s);
                """.formatted(
                tableName,
                String.join(", ", attributes),
                attributes
                        .stream()
                        .map(a -> "?")
                        .collect(Collectors.joining(", ")));
    }
}
