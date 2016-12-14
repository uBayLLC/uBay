-- Add these to reset the schema
DROP TABLE receipt;
DROP TABLE auction;
DROP TABLE bid;
DROP TABLE item;
DROP TABLE tag;
DROP TABLE account;

-- Create Statements
CREATE TABLE account (
    id INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(253) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    address VARCHAR(60) NOT NULL,
    card INT,
    PRIMARY KEY (id)
);

CREATE TABLE bid (
    id INT NOT NULL AUTO_INCREMENT,
    buyer_id INT NOT NULL,
    bid_amount INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (buyer_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE tag (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE item (
    id INT NOT NULL AUTO_INCREMENT, 
    name VARCHAR(30) NOT NULL,
    description VARCHAR(500) NOT NULL,
    photo VARCHAR(535) NOT NULL,
    seller_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (seller_id) REFERENCES account(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

CREATE TABLE auction (
    id INT NOT NULL AUTO_INCREMENT,
    item_id INT NOT NULL,
    starting_price INT NOT NULL,
    buy_out_price INT NOT NULL,
    bid_id INT NOT NULL,
    `end_datetime` DATETIME NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE,
    FOREIGN KEY (bid_id) REFERENCES bid(id) ON DELETE CASCADE
);

CREATE TABLE receipt (
    id INT NOT NULL AUTO_INCREMENT,
    card INT NOT NULL,
    auction_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (auction_id) REFERENCES auction(id) ON DELETE CASCADE
);

-- Insert test data
INSERT INTO account VALUES
	(NULL, 'miyagi@email.com', 'pw', 'Kesuke', 'Miyagi', 'Korea', '999999999'),
	(NULL, 'trump@email.com', 'pw', 'Donald', 'Trump', 'Murica', '123123123'),
	(NULL, 'hillary@email.com', 'pw','Hillary', 'Fucking Lost', 'Murica', '321321321'),
	(NULL, 'johnson@email.com', 'pw','Dwayne', 'Johnson', 'Murica', '456456456'),
	(NULL, 'harambe@email.com', 'pw', 'Harambe', 'Harambe', 'Heaven', '111111111');
	
INSERT INTO bid VALUES
	(NULL, '2', '25000'),
	(NULL, '2', '30000'),
	(NULL, '2', '50000'),
	(NULL, '1', '25'),
	(NULL, '3', '850'),
	(NULL, '5', '420');
	
INSERT INTO tag VALUES
	(NULL, 'Souls'),
	(NULL, 'People'),
	(NULL, 'Technology'),
	(NULL, 'Toys'),
	(NULL, 'Services');
	
INSERT INTO item VALUES
	(NULL, 'Fresh Soul #31', 'Fresh Souls curated just for you.', 'https://goo.gl/uY2Hf0', '1', '1'),
	(NULL, 'Fresh Soul #420', 'Blazit.', 'https://goo.gl/8NOEVb', '5', '1'),
	(NULL, 'The Karate Kid', 'He\'s only 11 though', 'https://goo.gl/wERXE3', '1', '2'),
	(NULL, 'A Rock Bottom', 'I\'ll give you a real one.', 'https://goo.gl/p9Q2zr', '4', '5');

INSERT INTO auction VALUES
	(NULL, '1', '500', '1000000', '2', '2016-06-01 00:00:00'),
	(NULL, '2', '42', '4200000', '3', '2017-01-10 00:00:00'),
	(NULL, '3', '10000', '3000000', '1', '2016-12-25 00:00:00'),
	(NULL, '4', '1000', '500000', '2', '2016-12-30 00:00:00');
	
INSERT INTO receipt VALUES
	(NULL, '123123123', '4'),
	(NULL, '321321321', '2');
