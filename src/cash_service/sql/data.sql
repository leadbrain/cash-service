--name: create-data-table!
CREATE TABLE IF NOT EXISTS data (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                 item VARCHAR(255),
                                 amount INT,
                                 from_type VARCHAR(255),
                                 from_id INT,
                                 to_type VARCHAR(255),
                                 to_id INT,
                                 create_time INT,
                                 input_time INT)

--name: get-data
SELECT input_time, item, amount, from_type, from_id, to_type, to_id
FROM data

--name: get-data-by-category
SELECT id, input_time, item, amount, from_type, from_id, to_type, to_id
FROM data
WHERE (from_type='category' AND from_id=(:category)) OR (to_type='category' AND to_id=(:category))

--name: update-data-category!
UPDATE data
SET category=(:category)
WHERE id=(:id)

--name: set-data<!
INSERT INTO data (item, amount, input_time, from_type, from_id, to_type, to_id)
VALUES (:item, :amount, :input_time, :from_type, :from_id, :to_type, :to_id)

--name: update-account-from-type!
UPDATE data
SET from_id=(:to)
WHERE from_type="account" AND from_id=(:from)

--name: update-account-to-type!
UPDATE data
SET to_id=(:to)
WHERE to_type="account" AND to_id=(:from)

--name: update-category-from-type!
UPDATE data
SET from_id=(:to)
WHERE from_type="category" AND from_id=(:from)

--name: update-category-to-type!
UPDATE data
SET to_id=(:to)
WHERE to_type="category" AND to_id=(:from)

--name: get-data-by-account
SELECT id, input_time, item, amount, from_type, from_id, to_type, to_id
FROM data
WHERE (from_type="account" AND from_id=(:account)) OR (to_type="account" AND to_id=(:account))

--name: drop-data-table!
DROP TABLE IF EXISTS data
