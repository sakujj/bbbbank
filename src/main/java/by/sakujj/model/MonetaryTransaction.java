package by.sakujj.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class MonetaryTransaction implements Entity<Long> {
    private Long id;
    private LocalDateTime timeWhenCommitted;
    private Long senderBankId;
    private Long receiverBankId;
    private String senderAccountId;
    private String receiverAccountId;
    private BigDecimal moneyAmount;
    private MonetaryTransactionType type;
}
