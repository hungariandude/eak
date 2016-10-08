CREATE TABLE item (
	id integer NOT NULL,
	name varchar(100) NOT NULL,
	price integer NOT NULL,

	PRIMARY KEY (id)
);

-- TEST DATA
INSERT INTO item (id, name, price) VALUES (101, 'Notebook', 300000);
INSERT INTO item (id, name, price) VALUES (102, 'PC', 200000);
INSERT INTO item (id, name, price) VALUES (103, 'Tablet', 100000);
INSERT INTO item (id, name, price) VALUES (104, 'Smartphone', 100000);
