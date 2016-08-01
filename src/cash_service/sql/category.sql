--name: create-category-table!
CREATE TABLE IF NOT EXISTS category (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                     name VARCHAR(255),
                                     type VARCHAR(255),
                                     money INT)

--name: get-category
SELECT id, name, type, money
FROM category

--name: get-category-by-id
SELECT name, type, money
FROM category
WHERE id=(:id)

--name: set-category<!
INSERT INTO category (name, type, money)
VALUES (:name, :type, :money)

--name: update-category-money!
UPDATE category
SET money=(:money)
WHERE id=(:id)

--name: delete-category!
DELETE FROM category
WHERE id=(:id)

--name: drop-category-table!
DROP TABLE IF EXISTS category
