package test.unit;

import by.sakujj.util.SQLQueries;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class SQLQueriesTests {

    @ParameterizedTest
    @MethodSource
    void getInsert(String tableName,
                                   List<String> attributes,
                                   String expected) {
        String actual = SQLQueries.getInsert(tableName, attributes);

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> getInsert() {
        return Stream.of(
                arguments(
                        "TABLE_X",
                        List.of("attr1", "attr2", "attr3"),
                        """
                                INSERT INTO TABLE_X
                                    (attr1, attr2, attr3)
                                VALUES
                                    (?, ?, ?);
                                """
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void getUpdateById(String tableName,
                                   String idAttributeName,
                                   List<String> attributesToUpdate,
                                   String expected) {
        String actual = SQLQueries.getUpdateById(
                tableName,
                idAttributeName,
                attributesToUpdate);

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> getUpdateById() {
        return Stream.of(
                arguments(
                        "TABLE_X",
                        "id_attr1",
                        List.of("attr1", "attr2", "attr3"),
                        """
                                UPDATE TABLE_X
                                \tSET
                                \tattr1 = ?,
                                \tattr2 = ?,
                                \tattr3 = ?
                                WHERE id_attr1 = ?;
                                """
                )
        );
    }
}

