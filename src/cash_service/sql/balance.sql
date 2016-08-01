--name: create-balance-table!
CREATE TABLE IF NOT EXISTS balance(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                   money INT)

--name: set-balance<!
INSERT INTO balance (money)
VALUES (:money)

--name: update-balance!
UPDATE balance
SET money=(:money)
WHERE id=(:id)

--name: get-balance
SELECT id, money
FROM balance

--name: drop-balance-table!
DROP TABLE IF EXISTS balance
