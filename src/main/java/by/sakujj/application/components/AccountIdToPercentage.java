package by.sakujj.application.components;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Setter
@Data
public class AccountIdToPercentage {
    String accountId;
    BigDecimal percent;
}