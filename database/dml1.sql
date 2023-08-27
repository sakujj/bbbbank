INSERT INTO Bank(bank_id, name)
VALUES (12345678910, 'Clever-bank');

INSERT INTO Bank(bank_id, name)
VALUES (12345678911, 'Belbank');

INSERT INTO Bank(bank_id, name)
VALUES (12345678912, 'Uganda-bank');

INSERT INTO Bank(bank_id, name)
VALUES (12345678913, 'QWE banking');

INSERT INTO Bank(bank_id, name)
VALUES (12345678914, 'Белорусский банкинг');

------------------------------------------------
INSERT INTO MonetaryTransactionType(type)
VALUES ('WITHDRAWAL');

INSERT INTO MonetaryTransactionType(type)
VALUES ('DEPOSIT');

INSERT INTO MonetaryTransactionType(type)
VALUES ('TRANSFER');
------------------------------------------------

----- THE PASSWORD TO ALL IS 'pass'
INSERT INTO Client(username, email, password)
VALUES ('user0', 'email0@gmail.com', '$2a$10$1mlM3e40rVQ8311QWex89Ozvy91BsmyVuM.bDbCcOjJJIUXMFpcMy');

INSERT INTO Client(username, email, password)
VALUES ('user1', 'email1@gmail.com', '$2a$10$ZTRludzpodYFUPbeuQwDUuh/E1fLIZhupn9Ql4Fg755NGTOUDqTnm');

INSERT INTO Client(username, email, password)
VALUES ('user2', 'email2@gmail.com', '$2a$10$N5LOdIiZE2H9Buje5sC6kuyV5i8vhw6yyN9jbPTXfpHkP/g7hIZ0W');

INSERT INTO Client(username, email, password)
VALUES ('user3', 'email3@gmail.com', '$2a$10$7hQ2f0E3bkY99MIKpLfAR.AUAFJEk9yoeFgi.ITSicahORaaCkF.y');

INSERT INTO Client(username, email, password)
VALUES ('user4', 'email4@gmail.com', '$2a$10$4D.386ys0rTLyVfRDTeIBek206uNIQG57ptvy2oF3B56nV1j/.gYG');

INSERT INTO Client(username, email, password)
VALUES ('user5', 'email5@gmail.com', '$2a$10$mUYfZSnWdad0Z6iwnEx5/uXbc/PKI1mmBMoa9tin5ygKMjdZ.j/Z.');

INSERT INTO Client(username, email, password)
VALUES ('user6', 'email6@gmail.com', '$2a$10$E8QzHI1Rn.kY/U/LWybQXumf8z37VNQYCn4ZT1GxIgwa646z/TPoi');

INSERT INTO Client(username, email, password)
VALUES ('user7', 'email7@gmail.com', '$2a$10$H1X6vhe0rMB.vmb4dqHVculRUQKLiIwlpIyz.qCDDToz2z6fDL4We');

INSERT INTO Client(username, email, password)
VALUES ('user8', 'email8@gmail.com', '$2a$10$3U/BcLhtIgFIkkTcycpwSuPoY4J.v91vHI1ARloUsu1XejUUy6pi6');

INSERT INTO Client(username, email, password)
VALUES ('user9', 'email9@gmail.com', '$2a$10$I3qFU7C6ip3yzHNQvB1J9unsdYQa1itnB4GvBMqmzhA64BoG6fgD.');

INSERT INTO Client(username, email, password)
VALUES ('user10', 'email10@gmail.com', '$2a$10$kQuNQPZyuVVh3mxjPULVtOIjcaOvL6mLbYw4u/o.KoP70XDsDKymu');

INSERT INTO Client(username, email, password)
VALUES ('user11', 'email11@gmail.com', '$2a$10$1JlAX2auiBkFD/SkDtRKy.qN9rSi7rhGIDo0scAHYBVpJgJ8rFwKq');

INSERT INTO Client(username, email, password)
VALUES ('user12', 'email12@gmail.com', '$2a$10$W7fj9LpZvhiF5WeYMVgJeOddmr18xw13CmPQn44m3FTSlz8O/.CWy');

INSERT INTO Client(username, email, password)
VALUES ('user13', 'email13@gmail.com', '$2a$10$lek9ZAlpjkS3cyrK1l2T9uNYVKl/VKRYRyTkAtxMsoxl6WoAfZ5qC');

INSERT INTO Client(username, email, password)
VALUES ('user14', 'email14@gmail.com', '$2a$10$9e7LEAAZjdpYR5a2XltkweDEyLRXrkflYaCiGtYXp59YqRIpmvHL2');

INSERT INTO Client(username, email, password)
VALUES ('user15', 'email15@gmail.com', '$2a$10$A/6bVdjGqisBHels8y4I8euWt6tS1bIMFICYsEEZtE8nky9yveJ9.');

INSERT INTO Client(username, email, password)
VALUES ('user16', 'email16@gmail.com', '$2a$10$ajMkdeeYkyZUkum//Z/7Zumgc4ebmdZ1f0jYtOLxoOQQ9W22BzSPa');

INSERT INTO Client(username, email, password)
VALUES ('user17', 'email17@gmail.com', '$2a$10$rbwb7ZhDX4A9loK12BI5tebEpJPfFdWQBKJB1IlEZu3894F07j95i');

INSERT INTO Client(username, email, password)
VALUES ('user18', 'email18@gmail.com', '$2a$10$RjsG68mOsy.N3TEDISslY.Fo2wECFszbm7RRIGXNORH.fl6/GamEu');

INSERT INTO Client(username, email, password)
VALUES ('user19', 'email19@gmail.com', '$2a$10$OPBGJTztRwkxpAqDfCixje9Icg60ueQBeb53F4AyC8PleBpATk.He');



---------------------------------------------------
INSERT INTO Currency(currency_id)
VALUES ('BYN');

INSERT INTO Currency(currency_id)
VALUES ('RUB');

INSERT INTO Currency(currency_id)
VALUES ('USD');

INSERT INTO Currency(currency_id)
VALUES ('EUR');

INSERT INTO Currency(currency_id)
VALUES ('CNY');
---------------------------------------------------

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX00',
        (SELECT client_id FROM Client c WHERE c.email = 'email0@gmail.com'),
        12345678910,
        '2000.0',
        'USD',
        '2020-01-10');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX01',
        (SELECT client_id FROM Client c WHERE c.email = 'email1@gmail.com'),
        12345678911,
        '2000.1',
        'BYN',
        '2020-02-11');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX02',
        (SELECT client_id FROM Client c WHERE c.email = 'email2@gmail.com'),
        12345678912,
        '2000.2',
        'BYN',
        '2020-03-12');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX03',
        (SELECT client_id FROM Client c WHERE c.email = 'email3@gmail.com'),
        12345678913,
        '2000.3',
        'USD',
        '2020-04-13');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX04',
        (SELECT client_id FROM Client c WHERE c.email = 'email4@gmail.com'),
        12345678914,
        '2000.4',
        'BYN',
        '2020-05-14');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX05',
        (SELECT client_id FROM Client c WHERE c.email = 'email5@gmail.com'),
        12345678910,
        '2000.5',
        'USD',
        '2020-06-15');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX06',
        (SELECT client_id FROM Client c WHERE c.email = 'email6@gmail.com'),
        12345678911,
        '2000.6',
        'USD',
        '2020-07-16');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX07',
        (SELECT client_id FROM Client c WHERE c.email = 'email7@gmail.com'),
        12345678912,
        '2000.7',
        'BYN',
        '2020-08-17');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX08',
        (SELECT client_id FROM Client c WHERE c.email = 'email8@gmail.com'),
        12345678913,
        '2000.8',
        'USD',
        '2020-09-18');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX09',
        (SELECT client_id FROM Client c WHERE c.email = 'email9@gmail.com'),
        12345678914,
        '2000.9',
        'USD',
        '2020-01-19');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX10',
        (SELECT client_id FROM Client c WHERE c.email = 'email10@gmail.com'),
        12345678911,
        '2000.10',
        'BYN',
        '2020-02-10');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX11',
        (SELECT client_id FROM Client c WHERE c.email = 'email11@gmail.com'),
        12345678912,
        '2000.11',
        'BYN',
        '2020-03-11');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX12',
        (SELECT client_id FROM Client c WHERE c.email = 'email12@gmail.com'),
        12345678913,
        '2000.12',
        'USD',
        '2020-04-12');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX13',
        (SELECT client_id FROM Client c WHERE c.email = 'email13@gmail.com'),
        12345678914,
        '2000.13',
        'BYN',
        '2020-05-13');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX14',
        (SELECT client_id FROM Client c WHERE c.email = 'email14@gmail.com'),
        12345678910,
        '2000.14',
        'USD',
        '2020-06-14');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX15',
        (SELECT client_id FROM Client c WHERE c.email = 'email15@gmail.com'),
        12345678911,
        '2000.15',
        'USD',
        '2020-07-15');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX16',
        (SELECT client_id FROM Client c WHERE c.email = 'email16@gmail.com'),
        12345678912,
        '2000.16',
        'BYN',
        '2020-08-16');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX17',
        (SELECT client_id FROM Client c WHERE c.email = 'email17@gmail.com'),
        12345678913,
        '2000.17',
        'BYN',
        '2020-09-17');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX18',
        (SELECT client_id FROM Client c WHERE c.email = 'email18@gmail.com'),
        12345678914,
        '2000.18',
        'USD',
        '2020-01-18');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX19',
        (SELECT client_id FROM Client c WHERE c.email = 'email19@gmail.com'),
        12345678910,
        '2000.19',
        'USD',
        '2020-02-19');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX20',
        (SELECT client_id FROM Client c WHERE c.email = 'email0@gmail.com'),
        12345678911,
        '2000.20',
        'USD',
        '2020-03-10');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX21',
        (SELECT client_id FROM Client c WHERE c.email = 'email1@gmail.com'),
        12345678912,
        '2000.21',
        'BYN',
        '2020-04-11');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX22',
        (SELECT client_id FROM Client c WHERE c.email = 'email2@gmail.com'),
        12345678913,
        '2000.22',
        'USD',
        '2020-05-12');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX23',
        (SELECT client_id FROM Client c WHERE c.email = 'email3@gmail.com'),
        12345678914,
        '2000.23',
        'USD',
        '2020-06-13');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX24',
        (SELECT client_id FROM Client c WHERE c.email = 'email4@gmail.com'),
        12345678910,
        '2000.24',
        'BYN',
        '2020-07-14');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX25',
        (SELECT client_id FROM Client c WHERE c.email = 'email5@gmail.com'),
        12345678911,
        '2000.25',
        'USD',
        '2020-08-15');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX26',
        (SELECT client_id FROM Client c WHERE c.email = 'email6@gmail.com'),
        12345678912,
        '2000.26',
        'BYN',
        '2020-09-16');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX27',
        (SELECT client_id FROM Client c WHERE c.email = 'email7@gmail.com'),
        12345678913,
        '2000.27',
        'BYN',
        '2020-01-17');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX28',
        (SELECT client_id FROM Client c WHERE c.email = 'email8@gmail.com'),
        12345678914,
        '2000.28',
        'BYN',
        '2020-02-18');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX29',
        (SELECT client_id FROM Client c WHERE c.email = 'email9@gmail.com'),
        12345678910,
        '2000.29',
        'USD',
        '2020-03-19');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX30',
        (SELECT client_id FROM Client c WHERE c.email = 'email10@gmail.com'),
        12345678911,
        '2000.30',
        'USD',
        '2020-04-10');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX31',
        (SELECT client_id FROM Client c WHERE c.email = 'email11@gmail.com'),
        12345678912,
        '2000.31',
        'USD',
        '2020-05-11');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX32',
        (SELECT client_id FROM Client c WHERE c.email = 'email12@gmail.com'),
        12345678913,
        '2000.32',
        'BYN',
        '2020-06-12');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX33',
        (SELECT client_id FROM Client c WHERE c.email = 'email13@gmail.com'),
        12345678914,
        '2000.33',
        'BYN',
        '2020-07-13');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX34',
        (SELECT client_id FROM Client c WHERE c.email = 'email14@gmail.com'),
        12345678910,
        '2000.34',
        'USD',
        '2020-08-14');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX35',
        (SELECT client_id FROM Client c WHERE c.email = 'email15@gmail.com'),
        12345678911,
        '2000.35',
        'USD',
        '2020-09-15');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX36',
        (SELECT client_id FROM Client c WHERE c.email = 'email16@gmail.com'),
        12345678912,
        '2000.36',
        'USD',
        '2020-01-16');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX37',
        (SELECT client_id FROM Client c WHERE c.email = 'email17@gmail.com'),
        12345678913,
        '2000.37',
        'BYN',
        '2020-02-17');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX38',
        (SELECT client_id FROM Client c WHERE c.email = 'email18@gmail.com'),
        12345678914,
        '2000.38',
        'BYN',
        '2020-03-18');

INSERT INTO Account (account_id, client_id, bank_id, money_amount, currency_id, date_when_opened)
VALUES ('123412341234123412341234XX39',
        (SELECT client_id FROM Client c WHERE c.email = 'email19@gmail.com'),
        12345678910,
        '2000.39',
        'USD',
        '2020-04-19');

