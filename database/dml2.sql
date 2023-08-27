
INSERT INTO MonetaryTransaction(time_when_committed, sender_account_id, receiver_account_id, money_amount, type)
VALUES ('2021-04-20 04:04:55',
        '123412341234123412341234XX39',
        '123412341234123412341234XX30',
        '1000.04',
        'TRANSFER');

INSERT INTO MonetaryTransaction(time_when_committed, sender_account_id, money_amount, type)
VALUES ('2021-04-20 04:04:55',
        '123412341234123412341234XX15',
        '1000.04',
        'WITHDRAWAL');

INSERT INTO MonetaryTransaction(time_when_committed, receiver_account_id, money_amount, type)
VALUES ('2021-04-20 04:04:55',
        '123412341234123412341234XX10',
        '6666.05',
        'DEPOSIT');