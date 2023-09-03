package by.sakujj.mappers;

import by.sakujj.dto.BankRequest;
import by.sakujj.dto.BankResponse;
import by.sakujj.model.Bank;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class BankMapper {

    public abstract Bank fromRequest(BankRequest bankRequest);
    public abstract BankResponse toResponse(Bank bank);
}
