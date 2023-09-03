package by.sakujj.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonetaryTransactionRequest {
    private Optional<String> senderAccountId;
    private Optional<String> receiverAccountId;
    private String moneyAmount;
    private String type;
}

