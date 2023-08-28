package by.sakujj.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonetaryTransactionRequest {
    private String senderAccountId;
    private String receiverAccountId;
    private String moneyAmount;
    private String type;
}

