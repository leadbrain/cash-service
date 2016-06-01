--name: create-data-table!
CREATE TABLE IF NOT EXISTS data (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                 item VARCHAR(255),
                                 money INT,
                                 category INT,
                                 create_time INT,
                                 input_time INT)

--name: create-category-table!
CREATE TABLE IF NOT EXISTS category (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                 name VARCHAR(255))

--name: get-data
SELECT input_time, item, money, category
FROM data

--name: set-data<!
INSERT into data (item, money, category, input_time) VALUES (:item, :money, :category, :input_time)

--name: get-category
SELECT id, name
FROM category

--name: set-category<!
INSERT into category (name) VALUES (:name)

--name: drop-table!
DROP TABLE IF EXISTS data

--name: drop-category-table!
DROP TABLE IF EXISTS category
