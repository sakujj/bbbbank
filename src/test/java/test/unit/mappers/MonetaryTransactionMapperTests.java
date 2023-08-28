package test.unit.mappers;

import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.mappers.MonetaryTransactionMapper;
import by.sakujj.model.MonetaryTransaction;
import by.sakujj.model.MonetaryTransactionType;
import org.junit.jupiter.params.ParameterizedTest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;


public class MonetaryTransactionMapperTests {
    private static final MonetaryTransactionMapper mapper = MonetaryTransactionMapper.getInstance();

   @ParameterizedTest
   @MethodSource
   void toMonetaryTransaction(MonetaryTransactionRequest request, MonetaryTransaction expected) {
       MonetaryTransaction actual = mapper.fromRequest(request);

       assertThat(actual).isEqualTo(expected);
   }

   static Stream<Arguments> toMonetaryTransaction() {
       String senderAccountId = "XXID232";
       String receiverAccountId = "YYID232";
       BigDecimal moneyAmount = new BigDecimal("444.45");
       MonetaryTransactionType type = MonetaryTransactionType.TRANSFER;

       return Stream.of(
               arguments(
                       MonetaryTransactionRequest.builder()
                               .senderAccountId(senderAccountId)
                               .receiverAccountId(receiverAccountId)
                               .moneyAmount(moneyAmount.toString())
                               .type(type.toString())
                               .build(),
                       MonetaryTransaction.builder()
                               .id(null)
                               .timeWhenCommitted(null)
                               .senderAccountId(senderAccountId)
                               .receiverAccountId(receiverAccountId)
                               .moneyAmount(moneyAmount)
                               .type(type)
                               .build()
               )
       );

   }

   @ParameterizedTest
   @MethodSource
   void toMonetaryTransactionResponse(MonetaryTransaction transaction, MonetaryTransactionResponse expected) {
       MonetaryTransactionResponse actual = mapper.toResponse(transaction);

       assertThat(actual).isEqualTo(expected);
   }
   static Stream<Arguments> toMonetaryTransactionResponse(){
       Long id = 44L;
       LocalDateTime timeWhenCommitted = LocalDateTime.now();
       String senderAccountId = "XXID232";
       String receiverAccountId = "YYID232";
       BigDecimal moneyAmount = new BigDecimal("444.45");
       MonetaryTransactionType type = MonetaryTransactionType.TRANSFER;
       return Stream.of(arguments(
               MonetaryTransaction.builder()
                       .id(id)
                       .timeWhenCommitted(timeWhenCommitted)
                       .senderAccountId(senderAccountId)
                       .receiverAccountId(receiverAccountId)
                       .moneyAmount(moneyAmount)
                       .type(type)
                       .build(),
               MonetaryTransactionResponse.builder()
                       .id(id.toString())
                       .timeWhenCommitted(timeWhenCommitted.toString())
                       .senderAccountId(senderAccountId)
                       .receiverAccountId(receiverAccountId)
                       .moneyAmount(moneyAmount.toString())
                       .type(type.toString())
                       .build()
       ));
   }

}
