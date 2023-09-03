package by.sakujj.services;

import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.model.MonetaryTransactionType;
import by.sakujj.util.ReceiptUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MonetaryTransactionServiceReceiptDecorator implements MonetaryTransactionService {
    private final MonetaryTransactionService service;

    private final ExecutorService pool;
    @Override
    public void close() {
        pool.shutdown();
    }

    @Override
    public Optional<MonetaryTransactionResponse> createTransferTransaction(MonetaryTransactionRequest request) {
        Optional<MonetaryTransactionResponse> possibleTransaction
                = service.createTransferTransaction(request);
        possibleTransaction.ifPresent(t ->
                pool.submit(() ->
                        this.printReceipt(t)
                )
        );
        return possibleTransaction;
    }

    @Override
    public Optional<MonetaryTransactionResponse> createDepositTransaction(MonetaryTransactionRequest request) {
        Optional<MonetaryTransactionResponse> possibleTransaction
                = service.createDepositTransaction(request);
        possibleTransaction.ifPresent(t ->
                pool.submit(() ->
                        this.printReceipt(t)
                )
        );
        return possibleTransaction;
    }

    @Override
    public Optional<MonetaryTransactionResponse> createWithdrawalTransaction(MonetaryTransactionRequest request) {
        Optional<MonetaryTransactionResponse> possibleTransaction
                = service.createWithdrawalTransaction(request);
        possibleTransaction.ifPresent(t ->
                pool.submit(() ->
                        this.printReceipt(t)
                )
        );
        return possibleTransaction;
    }

    @SneakyThrows
    public void printReceipt(MonetaryTransactionResponse response) {
        int lineLength = 60;
        List<String> lines = ReceiptUtil.parseTransactionToReceipt(response, lineLength);
        String documentPath = "check/receipt_%s.pdf".formatted(response.getId().toString());
        ReceiptUtil.printLinesToPdf(lines, documentPath);
    }



}
