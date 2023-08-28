package by.sakujj.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientRequest {
    private String username;
    private String email;
    private String notHashedPassword;
}
