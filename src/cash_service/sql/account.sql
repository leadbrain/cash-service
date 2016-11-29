--name: create-table!
CREATE TABLE IF NOT EXISTS account(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                   name VARCHAR(255),
                                   type VARCHAR(255),
                                   balance INT)

--name: insert-account<!
INSERT INTO account (name, type, balance)
VALUES (:name, :type, :balance)

--name: drop-table!
DROP TABLE IF EXISTS account

--name: get-accounts
SELECT id, name, type, balance
FROM account

--name: get-account
SELECT name, type, balance
FROM account
WHERE id=(:id)

--name: update-account!
UPDATE account
SET balance=(:balance)
WHERE id=(:id)

--name: delete-account!
DELETE FROM account
WHERE id=(:id)
