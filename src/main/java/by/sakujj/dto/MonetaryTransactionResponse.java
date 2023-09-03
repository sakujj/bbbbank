package by.sakujj.dto;

import by.sakujj.model.Currency;
import by.sakujj.model.MonetaryTransactionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonetaryTransactionResponse {
    private Long id;
    private LocalDateTime timeWhenCommitted;
    private Optional<String> senderAccountId;
    private Optional<String> receiverAccountId;
    private BigDecimal moneyAmount;
    private MonetaryTransactionType type;
    private Optional<String> bankSenderName;
    private Optional<String> bankReceiverName;
    private Currency currency;
}

