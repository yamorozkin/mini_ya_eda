-- Генерация 30 курьеров на 7 улицах
-- Улицы: Main Street, Oak Avenue, Pine Road, Elm Street, Maple Lane, Cedar Boulevard, Birch Way

INSERT INTO couriers (order_id, courier_name, eta_minutes, street, house_number, destination_street, destination_house_number) VALUES
-- Main Street (5 курьеров)
(0, 'Alex Johnson', NULL, 'Main Street', 15, NULL, NULL),
(0, 'Maria Garcia', NULL, 'Main Street', 27, NULL, NULL),
(0, 'David Chen', NULL, 'Main Street', 42, NULL, NULL),
(0, 'Sarah Wilson', NULL, 'Main Street', 8, NULL, NULL),
(0, 'Mike Brown', NULL, 'Main Street', 63, NULL, NULL),

-- Oak Avenue (4 курьера)
(0, 'Emma Davis', NULL, 'Oak Avenue', 22, NULL, NULL),
(0, 'James Miller', NULL, 'Oak Avenue', 35, NULL, NULL),
(0, 'Lisa Anderson', NULL, 'Oak Avenue', 51, NULL, NULL),
(0, 'Tom White', NULL, 'Oak Avenue', 74, NULL, NULL),

-- Pine Road (4 курьера)
(0, 'Anna Martinez', NULL, 'Pine Road', 18, NULL, NULL),
(0, 'Robert Taylor', NULL, 'Pine Road', 29, NULL, NULL),
(0, 'Karen Lee', NULL, 'Pine Road', 46, NULL, NULL),
(0, 'Chris Harris', NULL, 'Pine Road', 67, NULL, NULL),

-- Elm Street (4 курьера)
(0, 'Jessica Clark', NULL, 'Elm Street', 12, NULL, NULL),
(0, 'Daniel Rodriguez', NULL, 'Elm Street', 38, NULL, NULL),
(0, 'Nancy Lewis', NULL, 'Elm Street', 55, NULL, NULL),
(0, 'Paul Walker', NULL, 'Elm Street', 71, NULL, NULL),

-- Maple Lane (4 курьера)
(0, 'Rachel Hall', NULL, 'Maple Lane', 9, NULL, NULL),
(0, 'Kevin Young', NULL, 'Maple Lane', 24, NULL, NULL),
(0, 'Michelle King', NULL, 'Maple Lane', 41, NULL, NULL),
(0, 'Steven Wright', NULL, 'Maple Lane', 58, NULL, NULL),

-- Cedar Boulevard (4 курьера)
(0, 'Amanda Lopez', NULL, 'Cedar Boulevard', 16, NULL, NULL),
(0, 'Brian Hill', NULL, 'Cedar Boulevard', 33, NULL, NULL),
(0, 'Laura Green', NULL, 'Cedar Boulevard', 49, NULL, NULL),
(0, 'Mark Adams', NULL, 'Cedar Boulevard', 62, NULL, NULL),

-- Birch Way (5 курьеров)
(0, 'Jennifer Baker', NULL, 'Birch Way', 7, NULL, NULL),
(0, 'Jason Nelson', NULL, 'Birch Way', 21, NULL, NULL),
(0, 'Melissa Carter', NULL, 'Birch Way', 36, NULL, NULL),
(0, 'Ryan Mitchell', NULL, 'Birch Way', 52, NULL, NULL),
(0, 'Heather Perez', NULL, 'Birch Way', 69, NULL, NULL);
