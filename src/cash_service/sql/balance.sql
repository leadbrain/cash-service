--name: create-balance-table!
CREATE TABLE IF NOT EXISTS balance(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                   asset INT,
                                   debt INt)

--name: set-balance<!
INSERT INTO balance (asset, debt)
VALUES (:asset, :debt)

--name: update-asset!
UPDATE balance
SET asset=(:asset)
WHERE id=(:id)

--name: update-debt!
UPDATE balance
SET debt=(:debt)
WHERE id=(:id)

--name: get-balance
SELECT id, asset, debt
FROM balance

--name: drop-balance-table!
DROP TABLE IF EXISTS balance
