package test.unit.services;

import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.services.MonetaryTransactionServiceImpl;
import by.sakujj.services.MonetaryTransactionServiceReceiptDecorator;
import by.sakujj.util.ReceiptUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class MockitoTransactionServiceReceiptDecoratorTests {
    private AutoCloseable mockitoClosable;

    @Mock
    private MonetaryTransactionServiceImpl monetaryTransactionServiceImpl;
    @Mock
    private ExecutorService executorService;
    private MonetaryTransactionServiceReceiptDecorator monetaryTransactionServiceReceiptDecorator;

    @BeforeEach
    void mockitoSetup() {
        mockitoClosable = MockitoAnnotations.openMocks(this);
        monetaryTransactionServiceReceiptDecorator = Mockito.spy(
                new MonetaryTransactionServiceReceiptDecorator(monetaryTransactionServiceImpl, executorService)
        );
        Mockito.doNothing().when(monetaryTransactionServiceReceiptDecorator)
                .printReceipt(Mockito.any());
    }

    @AfterEach
    void mockitoCleanup() throws Exception {
        mockitoClosable.close();
    }

    @Test
    void shouldCreateDepositTransaction() {
        Mockito.doReturn(new CompletableFuture<>()).when(executorService).submit((Runnable) Mockito.any());
        Mockito.when(monetaryTransactionServiceImpl.createDepositTransaction(Mockito.any()))
                .thenReturn(Optional.of(MonetaryTransactionResponse.builder().build()));

        Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceReceiptDecorator
                .createDepositTransaction(MonetaryTransactionRequest.builder().build());
        assertThat(actual).isPresent();

    }

    @Test
    void shouldCreateWithdrawalTransaction() {
        Mockito.when(monetaryTransactionServiceImpl.createWithdrawalTransaction(Mockito.any()))
                .thenReturn(Optional.of(MonetaryTransactionResponse.builder().build()));

        Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceReceiptDecorator
                .createWithdrawalTransaction(MonetaryTransactionRequest.builder().build());
        assertThat(actual).isPresent();

    }

    @Test
    void shouldCreateTransferTransaction() {
        Mockito.when(monetaryTransactionServiceImpl.createTransferTransaction(Mockito.any()))
                .thenReturn(Optional.of(MonetaryTransactionResponse.builder().build()));

        Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceReceiptDecorator
                .createTransferTransaction(MonetaryTransactionRequest.builder().build());
        assertThat(actual).isPresent();
    }
}
