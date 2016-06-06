--name: create-data-table!
CREATE TABLE IF NOT EXISTS data (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                 item VARCHAR(255),
                                 money INT,
                                 category INT,
                                 create_time INT,
                                 input_time INT)

--name: create-category-table!
CREATE TABLE IF NOT EXISTS category (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                     name VARCHAR(255),
                                     type VARCHAR(255))

--name: create-balance-table!
CREATE TABLE IF NOT EXISTS balance(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                   money INT)

--name: get-data
SELECT input_time, item, money, category
FROM data

--name: get-data-by-category
SELECT id, input_time, item, money, category
FROM data
WHERE category=(:category)

--name: update-data-category!
UPDATE data
SET category=(:category)
WHERE id=(:id)

--name: set-data<!
INSERT into data (item, money, category, input_time)
VALUES (:item, :money, :category, :input_time)

--name: get-category
SELECT id, name, type
FROM category

--name: get-category-by-id
SELECT name, type
FROM category
WHERE id=(:id)

--name: set-category<!
INSERT into category (name, type)
VALUES (:name, :type)

--name: delete-category!
DELETE from category
WHERE id=(:id)

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

--name: drop-data-table!
DROP TABLE IF EXISTS data

--name: drop-category-table!
DROP TABLE IF EXISTS category

--name: drop-balance-table!
DROP TABLE IF EXISTS balance
