-- Insert Airlines
INSERT INTO AIRLINE (code, name) VALUES ('THY', 'Turkish Airlines');
INSERT INTO AIRLINE (code, name) VALUES ('PGS', 'Pegasus Airlines');
INSERT INTO AIRLINE (code, name) VALUES ('EZY', 'EasyJet');
INSERT INTO AIRLINE (code, name) VALUES ('RYR', 'Ryanair');
INSERT INTO AIRLINE (code, name) VALUES ('BAW', 'British Airways');
INSERT INTO AIRLINE (code, name) VALUES ('AFL', 'Aeroflot');
INSERT INTO AIRLINE (code, name) VALUES ('DLH', 'Lufthansa');
INSERT INTO AIRLINE (code, name) VALUES ('AFR', 'Air France');
INSERT INTO AIRLINE (code, name) VALUES ('KLM', 'KLM Royal Dutch');

-- Insert Airports
INSERT INTO AIRPORT (code, name) VALUES ('IST', 'Istanbul Airport');
INSERT INTO AIRPORT (code, name) VALUES ('SAW', 'Sabiha Gokcen Airport');
INSERT INTO AIRPORT (code, name) VALUES ('LHR', 'London Heathrow');
INSERT INTO AIRPORT (code, name) VALUES ('CDG', 'Charles de Gaulle');
INSERT INTO AIRPORT (code, name) VALUES ('AMS', 'Amsterdam Schiphol');
INSERT INTO AIRPORT (code, name) VALUES ('FRA', 'Frankfurt Airport');
INSERT INTO AIRPORT (code, name) VALUES ('SVO', 'Sheremetyevo Intl');
INSERT INTO AIRPORT (code, name) VALUES ('JFK', 'John F. Kennedy Intl');
INSERT INTO AIRPORT (code, name) VALUES ('DUB', 'Dublin Airport');
INSERT INTO AIRPORT (code, name) VALUES ('LGW', 'London Gatwick');

-- Insert Roles
INSERT INTO ROLE (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO ROLE (id, name) VALUES (2, 'ROLE_USER');
INSERT INTO ROLE (id, name) VALUES (3, 'ROLE_AIRLINE_STAFF');

-- Insert Users with BCrypt hashed passwords
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (1, 'user1', '$2a$10$8OfJhM7qU1cyyzBvjeFJcuU1brZ/Y6UvNLdQnAYAYwV6FjP0c4WfO', 'user1@example.com'); -- pass1
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (2, 'admin1', '$2a$10$AYrPf4y4okz9fbF25.J1EO.Wh4UgDb/Nu2UPUvrs1MiN1.YCkDLaq', 'admin1@example.com'); -- pass2
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (3, 'user2', '$2a$10$8xPb7uDLg4Dr1MTy9Z2ZQuHMnhCKx5M/Vnk9Id3CvL6EvcyBuxUpy', 'user2@example.com'); -- pass3
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (4, 'user3', '$2a$10$DbFxRxExBMeI6n7S8aW3mOR3Y9oMYYnB5s6RB9Z86ZkjNOznOYmE6', 'user3@example.com'); -- pass4
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (5, 'admin2', '$2a$10$hK9LU4JZyIjZV6WthZt9iOKND/bvHtHg0IPprT7KX37u3MVVDEZBq', 'admin2@example.com'); -- pass5
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (6, 'superadmin1', '$2a$10$sJ8T8rwvDJ2XsSJDq0zO0.VRIGkIGu4AWYFRfjnhchZev.Kq8jc1S', 'superadmin1@example.com'); -- pass6
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (7, 'user4', '$2a$10$4/UOhZ9CeQiQF9TpoU0wze2kDJ/og9BqFtJAj0aEG2.v8pPeEVx1y', 'user4@example.com'); -- pass7
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (8, 'user5', '$2a$10$ZyMwNSlYcX06FxVdJrRYMeUPR0kCtzYxwN6aGjV4zIhJP7EXWhCAW', 'user5@example.com'); -- pass8
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (9, 'admin3', '$2a$10$7bsZkZKwA0.MR29DSl0z7Ood/qV4blkuX5s6OjwZpQ2Mz07ISYZCW', 'admin3@example.com'); -- pass9
INSERT INTO APP_USER (id, username, hashed_password, email) VALUES (10, 'user6', '$2a$10$YBQKH7HSAU2XoWIkylmgoeQOWZn.Xb6qx3RU7VvMsXjNRgBoZpNgS', 'user6@example.com'); -- pass10


-- Insert User Roles
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (5, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (5, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (6, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (6, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (7, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (8, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (9, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (9, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (10, 1);

-- Insert Flights
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (1, '2025-06-20 09:00:00', 'THY', 'IST', 'LHR');
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (2, '2025-06-21 14:30:00', 'PGS', 'SAW', 'CDG');
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (3, '2025-06-22 12:00:00', 'THY', 'IST', 'JFK');
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (4, '2025-06-19 07:45:00', 'EZY', 'LGW', 'AMS');
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (5, '2025-06-23 11:15:00', 'RYR', 'DUB', 'FRA');
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (6, '2025-06-24 10:00:00', 'BAW', 'LHR', 'IST');
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (7, '2025-06-25 16:00:00', 'AFL', 'SVO', 'SAW');
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (8, '2025-06-20 13:30:00', 'DLH', 'FRA', 'CDG');
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (9, '2025-06-21 18:15:00', 'AFR', 'CDG', 'AMS');
INSERT INTO FLIGHT (id, departure_time, airline_code, src_airport_code, dest_airport_code) VALUES (10, '2025-06-22 21:00:00', 'KLM', 'AMS', 'IST');
