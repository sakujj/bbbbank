package by.sakujj.util;

import by.sakujj.model.Account;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AccountIdGenerator {
    public static String generateAccountId(int accountNumber, Long bankId, Long clientId) {
        StringBuilder builder = new StringBuilder();

        while (builder.length() != 15 - String.valueOf(clientId).length()) {
            builder.append("X");
        }
        builder.append(clientId);
        builder.insert(0, accountNumber);
        if (accountNumber / 10 == 0)
            builder.insert(0, 0);
;
        return  "%s%s".formatted( bankId,
                builder.toString());
    }
}
