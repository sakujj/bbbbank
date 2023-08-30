package by.sakujj.services;


import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.BankDAO;
import by.sakujj.dto.BankResponse;
import by.sakujj.mappers.BankMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankServiceImpl implements BankService {
    private final ConnectionPool connectionPool;
    private final BankMapper bankMapper;
    private final BankDAO bankDAO;

    @SneakyThrows
    public List<BankResponse> findAll(){
        try(Connection connection = connectionPool.getConnection()) {
            List<BankResponse> bankResponses = bankDAO.findAll(connection)
                    .stream()
                    .map(bankMapper::toResponse)
                    .toList();
            return bankResponses;
        }
    }
}
