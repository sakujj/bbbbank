package by.sakujj.application.components;

import by.sakujj.context.ApplicationContext;
import by.sakujj.dto.*;
import by.sakujj.exceptions.ValidationException;
import by.sakujj.model.Currency;
import by.sakujj.model.MonetaryTransactionType;
import by.sakujj.services.*;
import by.sakujj.validators.MonetaryTransactionValidator;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BankApplication {
    private static final ApplicationContext context = ApplicationContext.getInstance();

    private final ClientResponse authenticatedUser;
    private final AccountService accountService = context.getByClass(AccountService.class);
    private final BankService bankService = context.getByClass(BankService.class);
    private final MonetaryTransactionService monetaryTransactionService
            = context.getByClass(MonetaryTransactionService.class);
    private final MonetaryTransactionValidator monetaryTransactionValidator
            = context.getByClass(MonetaryTransactionValidator.class);

    public void start() {
        try {
            if (authenticatedUser == null) {
                System.out.println("(!) Quiting");
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String output;
            String input;
            do {
                System.out.println("""
                        Type:
                        \t'my a' to view your accounts
                        \t'new a' to create new account
                        \t'all a' to see all accounts
                        \t'all b' to see all banks
                        \t'q' to quit
                        \t't' to transfer money
                        \t'd' to deposit money
                        \t'w' to withdrawal money
                        """);

                input = reader.readLine();
                output = switch (input) {
                    case "my a" -> {
                        String myAccounts = getMyAccounts();
                        if (myAccounts.isEmpty())
                            yield "No accounts are present";
                        yield myAccounts;
                    }
                    case "new a" -> createNewAccount(reader);
                    case "all a" -> getAllAccounts();
                    case "all b" -> getAllBanks();
                    case "d" -> depositToAccount(reader);
                    case "w" -> withdrawFromAccount(reader);
                    case "t" -> transferToAccount(reader);
                    case "q" -> "Goodbye %s, %s".formatted(
                            authenticatedUser.getUsername(),
                            authenticatedUser.getEmail());
                    default -> "Wrong input, try again";
                };
                System.out.printf("%n%s%n%n", output);
            } while (!"q".equals(input));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String transferToAccount(BufferedReader reader) throws IOException {
        System.out.println("Your accounts: ");
        System.out.println(getMyAccounts());

        System.out.println("Specify your account ID to TRANSFER from: ");
        String myAccountId = reader.readLine();

        List<AccountResponse> myAccounts = accountService.findAllByClientId(authenticatedUser.getId());
        Optional<AccountResponse> possibleMyAccount = myAccounts.stream()
                .filter(a -> a.getId().equals(myAccountId))
                .findAny();
        if (possibleMyAccount.isEmpty()) {
            return "You can TRANSFER only from your accounts";
        }
        AccountResponse myAccount = possibleMyAccount.get();

        System.out.println("All accounts: ");
        System.out.println(getAllAccounts());

        System.out.println("Specify the ID of an account to TRANSFER to: ");
        String otherAccountId = reader.readLine();

        System.out.print("Specify money amount to TRANSFER: ");
        String moneyAmount = reader.readLine();

        MonetaryTransactionType type = MonetaryTransactionType.TRANSFER;
        MonetaryTransactionRequest request = MonetaryTransactionRequest.builder()
                .moneyAmount(moneyAmount)
                .senderAccountId(Optional.of(myAccountId))
                .receiverAccountId(Optional.of(otherAccountId))
                .type(type.toString())
                .build();
        try {
            monetaryTransactionValidator.validate(request);
        } catch (ValidationException e) {
            return getFormattedErrors(e);
        }

        System.out.printf("""
                Are you sure to TRANSFER %s %s
                from your account:
                    %s
                to other account:
                    %s ?
            yes/y or no/n: """.formatted(moneyAmount, myAccount.getCurrency(),
                myAccountId,
                otherAccountId));
        String answer = reader.readLine();

        if (!answer.equals("yes") && !answer.equals("y"))
            return "TRANSFER canceled";


        Optional<MonetaryTransactionResponse> possibleResponse
                = monetaryTransactionService.createTransferTransaction(request);
        return possibleResponse.map(r -> "Successful transaction performed")
                .orElse("Error occurred, transaction cancelled");
    }

    public String depositToAccount(BufferedReader reader) throws IOException {
        System.out.println("Your accounts: ");
        System.out.println(getMyAccounts());

        System.out.print("Specify the id of an account to DEPOSIT: ");
        String accountId = reader.readLine();

        System.out.print("Specify money amount to DEPOSIT: ");
        String moneyAmount = reader.readLine();

        MonetaryTransactionType type = MonetaryTransactionType.DEPOSIT;
        MonetaryTransactionRequest request = MonetaryTransactionRequest.builder()
                .senderAccountId(Optional.empty())
                .receiverAccountId(Optional.of(accountId))
                .type(type.toString())
                .moneyAmount(moneyAmount)
                .build();
        try {
            monetaryTransactionValidator.validate(request);
        } catch (ValidationException e) {
            return getFormattedErrors(e);
        }
        AccountResponse accountToDeposit = accountService.findById(accountId).get();

        System.out.printf("""
                    Are you sure you want to DEPOSIT %s %s
                    to %s account of %s?
                yes/y or no/n: """, moneyAmount, accountToDeposit.getCurrency(),
                accountId,
                accountToDeposit.getClientEmail());
        String answer = reader.readLine();

        if (!answer.equals("yes") && !answer.equals("y"))
            return "DEPOSIT canceled";

        Optional<MonetaryTransactionResponse> response = monetaryTransactionService
                .createDepositTransaction(request);

        return response.map(r -> "Successful transaction performed")
                .orElse("Error occurred, transaction canceled");
    }

    public String withdrawFromAccount(BufferedReader reader) throws IOException {
        System.out.println("Your accounts: ");
        System.out.println(getMyAccounts());

        System.out.print("Specify the id of an account to WITHDRAW from: ");
        String accountId = reader.readLine();


        List<AccountResponse> myAccounts = accountService.findAllByClientId(authenticatedUser.getId());
        Optional<AccountResponse> specifiedAccount = myAccounts.stream()
                .filter(a -> a.getId().equals(accountId))
                .findAny();
        if (specifiedAccount.isEmpty()) {
            return "You can WITHDRAW only from your accounts";
        }

        System.out.print("Specify money amount to withdraw: ");
        String moneyAmount = reader.readLine();


        MonetaryTransactionType type = MonetaryTransactionType.WITHDRAWAL;
        MonetaryTransactionRequest request = MonetaryTransactionRequest.builder()
                .senderAccountId(Optional.of(accountId))
                .receiverAccountId(Optional.empty())
                .type(type.toString())
                .moneyAmount(moneyAmount)
                .build();
        try {
            monetaryTransactionValidator.validate(request);
        } catch (ValidationException e) {
            return getFormattedErrors(e);
        }

        AccountResponse accountToWithdrawFrom = specifiedAccount.get();

        System.out.printf("""
                            Are you sure you want to WITHDRAW %s %s
                            from %s account with balance %s?
                        yes/y or no/n: """,
                moneyAmount, accountToWithdrawFrom.getCurrency(),
                accountId,
                accountToWithdrawFrom.getMoneyAmount());
        String answer = reader.readLine();

        if (!answer.equals("yes") && !answer.equals("y"))
            return "WITHDRAWAL canceled";

        Optional<MonetaryTransactionResponse> response = monetaryTransactionService
                .createWithdrawalTransaction(request);
        return response.map(r -> "Successful transaction performed")
                .orElse("Error occurred, transaction canceled");
    }

    public String createNewAccount(BufferedReader reader) throws IOException {
        System.out.printf("Creating new account for %s: %n", authenticatedUser.getEmail());
        System.out.println(getAllBanks());
        System.out.print("\tBank ID: ");
        String bankIdString = reader.readLine();
        System.out.printf("\tAccount currency(%s): ", Arrays.toString(Currency.values()));
        String currency = reader.readLine();
        AccountRequest request = AccountRequest.builder()
                .currency(currency)
                .bankId(bankIdString)
                .clientEmail(authenticatedUser.getEmail())
                .build();
        String accountId = accountService.save(request);
        return "Successfully created account with ID %s".formatted(accountId);
    }

    public String getMyAccounts() {
        List<AccountResponse> accounts = accountService.findAllByClientId(authenticatedUser.getId());
        return accounts.stream()
                .map(a -> """
                        Account ID: %s, Bank ID: %s, Money amount: %s %s, Date when opened: %s
                        """.formatted(
                        a.getId(),
                        a.getBankId(),
                        a.getMoneyAmount(), a.getCurrency(),
                        a.getDateWhenOpened()
                ))
                .collect(Collectors.joining(""));
    }

    public String getAllAccounts() {
        List<AccountResponse> all = accountService.findAll();
        return all.stream()
                .map(
                        a -> "ID: %s, EMAIL: %s, MONEY: %s%s"
                                .formatted(a.getId(),
                                        a.getClientEmail(),
                                        a.getMoneyAmount(), a.getCurrency()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public String getAllBanks() {
        List<BankResponse> all = bankService.findAll();
        return all.stream()
                .map(
                        b -> "ID: %s, BANK NAME: %s"
                                .formatted(b.getId(),
                                        b.getName()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String getFormattedErrors(ValidationException e) {
        return e.getErrors()
                .stream()
                .map(err -> "(!) " + err)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
