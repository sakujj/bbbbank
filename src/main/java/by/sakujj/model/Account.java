package by.sakujj.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Account implements Entity<String>{
    private String id;
    private Long clientId;
    private Long bankId;
    private BigDecimal moneyAmount;
    private Currency currency;
    private LocalDate dateWhenOpened;
}
