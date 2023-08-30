package by.sakujj.dto;

import by.sakujj.model.Currency;
import by.sakujj.model.MonetaryTransactionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonetaryTransactionResponse {
    private Long id;
    private LocalDateTime timeWhenCommitted;
    private String senderAccountId;
    private String receiverAccountId;
    private BigDecimal moneyAmount;
    private MonetaryTransactionType type;
    private String bankSenderName;
    private String bankReceiverName;
    private Currency currency;
}

