package by.sakujj.dto;

import by.sakujj.model.Currency;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponse {
    private String id;
    private String clientEmail;
    private Long bankId;
    private Currency currency;
    private BigDecimal moneyAmount;
    private LocalDate dateWhenOpened;
}
