DELETE FROM book;
ALTER TABLE book AUTO_INCREMENT = 1001;

DELETE FROM category;
ALTER TABLE category AUTO_INCREMENT = 1001;

INSERT INTO `category` (`name`) VALUES ('Science Fiction'),('Fantasy'),('Classic'),('Horror');

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Origin of Time', 'Thomas Hetrog', 'It is a book about time', 6.99, 10, TRUE, TRUE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Brave New World', 'Aldous Huxley', 'Book about stuff', 7.99, 9, FALSE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Dark Forest', 'Cixin Liu', 'WOW we are not alone', 5.99, 9, TRUE, TRUE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('1984', 'George Orwell', '1984', 6.659, 8, TRUE, FALSE, 1001);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Hobbit', 'J. R. R. Tolkien', 'They travel and do quest', 11.59, 10, FALSE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('A Game of Thrones', 'George R. R. Martin', 'Mind games and stuff', 25.64, 9, FALSE, TRUE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Dune', 'Frank Herbert', 'Lisan Al Gahib!!!', 3.9, 9, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Lord of the Rings', 'J. R. R. Tolkien', 'More traveling more quest', 6.00, 9, TRUE, TRUE, 1002);


INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Great Gatsby', 'F.Scott Fitzgerald', 'Obessive man', 12.59, 8, TRUE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Moby Dick', 'Herman Melville', 'Whale', 7.99, 9, TRUE, TRUE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('To Kill a Mockingbird', 'Harper Lee', 'Bird', 5.99, 8, FALSE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Pride and Prejudice', 'Jane Austin', 'British people love drama', 6.99, 8, FALSE, TRUE, 1003);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Shinning', 'Steven King', 'The deranged husband',7.99, 8, FALSE, TRUE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Dracula', 'Bram Stoker', 'Blood sucker', 2.99, 7, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Exorcist', 'William Peter Blatty', 'weee im getting possesed', 12.99, 7, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Coraline', 'Steven King', 'the film was great',15, 7, TRUE, FALSE, 1004);