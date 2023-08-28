package by.sakujj.mappers;

import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.model.MonetaryTransaction;
import by.sakujj.model.MonetaryTransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public abstract class MonetaryTransactionMapper {
    private static final MonetaryTransactionMapper INSTANCE = Mappers.getMapper(MonetaryTransactionMapper.class);
    
    public static MonetaryTransactionMapper getInstance() {
        return INSTANCE;
    }

    public abstract MonetaryTransaction fromRequest(MonetaryTransactionRequest request);
    public abstract MonetaryTransactionResponse toResponse(MonetaryTransaction monetaryTransaction);
}
