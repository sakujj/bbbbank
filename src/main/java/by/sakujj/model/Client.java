package by.sakujj.model;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Client implements Entity<Long> {
    private Long id;
    private String username;
    private String email;
    private String password;
}