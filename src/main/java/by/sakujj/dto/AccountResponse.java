package by.sakujj.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponse {
    private String id;
    private String clientEmail;
    private String bankId;
    private String currency;
    private String moneyAmount;
    private String dateWhenOpened;
}
