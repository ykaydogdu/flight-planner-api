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

-- Insert Users with BCrypt hashed passwords
INSERT INTO APP_USER (username, password, email, role) VALUES ('user1', '$2y$12$zkiAcfx1qwgxVPxKTbzbl.j7u9wfrUtyfjO/e4u414mZMDmimcrtG', 'user1@example.com', 'ROLE_USER'); -- pass1
INSERT INTO APP_USER (username, password, email, role) VALUES ('admin1', '$2y$12$mr/xmdBFqZJKe79x6WNGruc9qx9xTV8xX0HRsGtIzfaIg6xl3GACG', 'admin1@example.com', 'ROLE_ADMIN'); -- pass2
INSERT INTO APP_USER (username, password, email, role) VALUES ('user2', '$2y$12$Xwx.qGQe7Vupvre70r1OU.n3757uBzI0zrwVbbtShLpyDUzvbgnue', 'user2@example.com', 'ROLE_USER'); -- pass3
INSERT INTO APP_USER (username, password, email, role) VALUES ('user3', '$2y$12$sG/BXQwdLwNTWCL8NoxhPuOnt2lXqhgX64DBSeW4h5r8oPjNegnee', 'user3@example.com', 'ROLE_USER'); -- pass4
INSERT INTO APP_USER (username, password, email, role) VALUES ('admin2', '$2y$12$zNf7czeuW4Nff7rrsDBt9.Thh1l9SM9dNwWaeEchCnQuMHNRdM3Z.', 'admin2@example.com', 'ROLE_ADMIN'); -- pass5
INSERT INTO APP_USER (username, password, email, role, airline_code) VALUES ('staff1', '$2y$12$svLcCLqA/ZFrkjr4RGGRg.vG6t7mdf.dyz.hLUjv.CCFNPBy1Gxc.', 'staff1@example.com', 'ROLE_AIRLINE_STAFF', 'THY'); -- pass6
INSERT INTO APP_USER (username, password, email, role) VALUES ('user4', '$2y$12$wmxhO8axePI7R4oweApq9e6roXcSZB3aPu4jB2FvBXOBfYVMESUla', 'user4@example.com', 'ROLE_USER'); -- pass7
INSERT INTO APP_USER (username, password, email, role) VALUES ('user5', '$2y$12$CIFZ1.lxBYbN1.yXLAkane9tF09vhI88H5sF0pUcFGdyyDDAApKmi', 'user5@example.com', 'ROLE_USER'); -- pass8
INSERT INTO APP_USER (username, password, email, role) VALUES ('admin3', '$2y$12$AKBMh07xI5PH.X9FzIrDTOafLFzL.MF444ChpZnioNV0WnUy9xzjS', 'admin3@example.com', 'ROLE_ADMIN'); -- pass9
INSERT INTO APP_USER (username, password, email, role) VALUES ('user6', '$2y$12$W0htFDH09d5ajinx8zjeSOBZHEnkuPyAX3w0ktehVfrEnAcB8pzmO', 'user6@example.com', 'ROLE_USER'); -- pass10

-- Insert Flights
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (1, '2025-06-20 09:00:00', 'THY', 'IST', 'LHR', 150.00, 50);
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (2, '2025-06-21 14:30:00', 'PGS', 'SAW', 'CDG', 120.00, 30);
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (3, '2025-06-22 12:00:00', 'THY', 'IST', 'JFK', 300.00, 20);
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (4, '2025-06-19 07:45:00', 'EZY', 'LGW', 'AMS', 80.00, 100);
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (5, '2025-06-23 11:15:00', 'RYR', 'DUB', 'FRA', 90.00, 70);
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (6, '2025-06-24 10:00:00', 'BAW', 'LHR', 'IST', 200.00, 40);
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (7, '2025-06-25 16:00:00', 'AFL', 'SVO', 'SAW', 250.00, 60);
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (8, '2025-06-20 13:30:00', 'DLH', 'FRA', 'CDG', 180.00, 80);
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (9, '2025-06-21 18:15:00', 'AFR', 'CDG', 'AMS', 160.00, 90);
INSERT INTO FLIGHT (id, departure_time, airline_code, origin_airport_code, destination_airport_code, price, seat_count) VALUES (10, '2025-06-22 21:00:00', 'KLM', 'AMS', 'IST', 220.00, 25);
ALTER TABLE FLIGHT ALTER COLUMN id RESTART WITH 11;