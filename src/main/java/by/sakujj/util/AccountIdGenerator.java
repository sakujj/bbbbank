package by.sakujj.util;

import by.sakujj.model.Account;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AccountIdGenerator {
    public static String generateAccountId(Long bankId, Long clientId) {
        String clientIdPart = String.valueOf(clientId % (10L * 17));
        StringBuilder builder = new StringBuilder(clientIdPart);
        while (builder.length() != 17) {
            builder.insert(0,"X");
        }

        return  "%s%s%s".formatted( bankId,
                clientId % 10_000_000_000L,
                builder.toString());
    }
}
