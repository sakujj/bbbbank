package by.sakujj.services;

import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.model.MonetaryTransactionType;
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

    private final ExecutorService pool = Executors.newFixedThreadPool(1);
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
        List<String> lines = parseTransactionToReceipt(response, lineLength);
        String documentPath = "check/receipt_%s.pdf".formatted(response.getId().toString());
        printLinesToPdf(lines, documentPath);
    }

    private List<String> parseTransactionToReceipt(MonetaryTransactionResponse response, int lineLength) {
        Long transactionId = response.getId();
        LocalDateTime timeWhenCommitted = response.getTimeWhenCommitted();
        Optional<String> senderAccountId = response.getSenderAccountId();
        Optional<String> receiverAccountId = response.getReceiverAccountId();
        Optional<String> bankSenderName = response.getBankSenderName();
        Optional<String> bankReceiverName = response.getBankReceiverName();
        MonetaryTransactionType type = response.getType();
        BigDecimal moneyAmount = response.getMoneyAmount();


        List<String> lines = new ArrayList<>();
        String horizontalBorder = "-".repeat(lineLength);
        String property;
        String value;
        lines.add(horizontalBorder);

        String header = "Банковский чек";
        lines.add(createReceiptHeader(lineLength, header));

        property = "Чек: ";
        value = String.valueOf(transactionId);
        lines.add(createReceiptLine(lineLength, property, value));

        property = timeWhenCommitted.toLocalDate().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")
        );
        value = timeWhenCommitted.toLocalTime().format(
                DateTimeFormatter.ofPattern("HH:mm:ss")
        );
        lines.add(createReceiptLine(lineLength, property, value));

        property = "Тип транзакции:";
        value = switch (type) {
            case DEPOSIT -> "Начисление";
            case WITHDRAWAL -> "Снятие";
            case TRANSFER -> "Перевод";
        };
        lines.add(createReceiptLine(lineLength, property, value));

        if (bankSenderName.isPresent()) {
            property = "Банк отправителя:";
            value = bankSenderName.get();
            lines.add(createReceiptLine(lineLength, property, value));
        }

        if (bankReceiverName.isPresent()) {
            property = "Банк получателя:";
            value = bankReceiverName.get();
            lines.add(createReceiptLine(lineLength, property, value));
        }

        if (senderAccountId.isPresent()) {
            property = "Счет оптравителя:";
            value = senderAccountId.get();
            lines.add(createReceiptLine(lineLength, property, value));
        }

        if (receiverAccountId.isPresent()) {
            property = "Счет получателя:";
            value = receiverAccountId.get();
            lines.add(createReceiptLine(lineLength, property, value));
        }

        property = "Сумма:";
        value = moneyAmount.toString() + " " + response.getCurrency();
        lines.add(createReceiptLine(lineLength, property, value));

        lines.add(horizontalBorder);

        return lines;
    }

    private void printLinesToPdf(List<String> lines, String documentPath) throws IOException {
        PDDocument document = new PDDocument();
        PDType0Font font = PDType0Font.load(document, new File("fonts/Hack-Regular.ttf"));


        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(font, 14);

        contentStream.beginText();

        contentStream.setLeading(18f);
        contentStream.newLineAtOffset(25, 725);

        for (String l : lines) {
            contentStream.showText(l);
            contentStream.newLine();
        }

        contentStream.endText();
        contentStream.close();

        document.save(documentPath);
        document.close();

    }

    private String createReceiptHeader(int lineLength, String header) {
        int headerLength = header.length();
        if (lineLength < headerLength) {
            throw new IllegalArgumentException();
        }
        int fillerLength = (lineLength - headerLength) / 2;
        int residue = (lineLength - headerLength) % 2;

        return "|" + " ".repeat(fillerLength - 1 + residue)
                + header
                + " ".repeat(fillerLength - 1) + "|";
    }

    private String createReceiptFiller(int lineLength, int propertyLength, int valueLength) {
        return " ".repeat(
                lineLength
                        - propertyLength
                        - valueLength
                        - 2
        );
    }

    private String createReceiptLine(int lineLength, String property, String value) {
        String filler = createReceiptFiller(lineLength, property.length(), value.length());
        return "|%s%s%s|".formatted(property, filler, value);
    }


}
