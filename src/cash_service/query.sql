--name: create-table!
CREATE TABLE IF NOT EXISTS data (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                 item VARCHAR(255),
                                 money INT)

--name: get-data
SELECT item, money
FROM data

--name: set-data<!
INSERT into data (item, money) VALUES (:item, :money)

--name: drop-table!
DROP TABLE IF EXISTS data
