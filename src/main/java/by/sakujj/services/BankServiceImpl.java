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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankServiceImpl implements BankService {
    private final ConnectionPool connectionPool;
    private final BankMapper bankMapper;
    private final BankDAO bankDAO;

    @SneakyThrows
    public Optional<BankResponse> findById(Long id) {
        try (Connection connection = connectionPool.getConnection()) {
            return bankDAO.findById(id, connection)
                    .map(bankMapper::toResponse);
        }
    }

    @SneakyThrows
    public Optional<BankResponse> findByName(String name) {
        try (Connection connection = connectionPool.getConnection()) {
            return bankDAO.findByName(name, connection)
                    .map(bankMapper::toResponse);
        }
    }
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
