--name: create-data-table!
CREATE TABLE IF NOT EXISTS data (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                 item VARCHAR(255),
                                 money INT,
                                 category INT,
                                 account INT,
                                 create_time INT,
                                 input_time INT)

--name: get-data
SELECT input_time, item, money, category, account
FROM data

--name: get-data-by-category
SELECT id, input_time, item, money, category, account
FROM data
WHERE category=(:category)

--name: update-data-category!
UPDATE data
SET category=(:category)
WHERE id=(:id)

--name: set-data<!
INSERT INTO data (item, money, category, input_time, account)
VALUES (:item, :money, :category, :input_time, :account)

--name: update-account!
UPDATE data
SET account=(:to)
WHERE account=(:from)

--name: update-category!
UPDATE data
SET category=(:to)
WHERE category=(:from)

--name: get-data-by-account
SELECT id, input_time, item, money, category, account
FROM data
WHERE account=(:account)

--name: get-data-by-category
SELECT id, input_time, item, money, category, account
FROM data
WHERE category=(:category)

--name: drop-data-table!
DROP TABLE IF EXISTS data
