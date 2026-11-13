INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Standard', 3500.00, 5 FROM hotels h
WHERE h.name = 'Seabreeze Palm Resort'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Standard');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 6300.00, 2 FROM hotels h
WHERE h.name = 'Seabreeze Palm Resort'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Deluxe', 3600.00, 3 FROM hotels h
WHERE h.name = 'Coconut Grove Inn'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Deluxe');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 5000.00, 1 FROM hotels h
WHERE h.name = 'Coconut Grove Inn'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Standard', 5000.00, 5 FROM hotels h
WHERE h.name = 'Harbor View Suites'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Standard');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 9000.00, 2 FROM hotels h
WHERE h.name = 'Harbor View Suites'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Deluxe', 4200.00, 4 FROM hotels h
WHERE h.name = 'Capital Courtyard Hotel'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Deluxe');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 5800.00, 2 FROM hotels h
WHERE h.name = 'Capital Courtyard Hotel'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Deluxe', 3900.00, 4 FROM hotels h
WHERE h.name = 'Garden City Stay'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Deluxe');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 5400.00, 1 FROM hotels h
WHERE h.name = 'Garden City Stay'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Standard', 4000.00, 5 FROM hotels h
WHERE h.name = 'Lake Palace Retreat'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Standard');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 7200.00, 2 FROM hotels h
WHERE h.name = 'Lake Palace Retreat'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Standard', 2900.00, 5 FROM hotels h
WHERE h.name = 'Backwater Haven'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Standard');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 5200.00, 1 FROM hotels h
WHERE h.name = 'Backwater Haven'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Standard', 3200.00, 5 FROM hotels h
WHERE h.name = 'Pine Valley Lodge'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Standard');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 5800.00, 1 FROM hotels h
WHERE h.name = 'Pine Valley Lodge'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Standard', 2900.00, 5 FROM hotels h
WHERE h.name = 'Ganga View Resort'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Standard');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 5200.00, 1 FROM hotels h
WHERE h.name = 'Ganga View Resort'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Standard', 3000.00, 5 FROM hotels h
WHERE h.name = 'Deccan Heights Hotel'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Standard');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 5400.00, 1 FROM hotels h
WHERE h.name = 'Deccan Heights Hotel'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Standard', 3300.00, 5 FROM hotels h
WHERE h.name = 'Marina Bay Hotel'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Standard');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT h.id, 'Suite', 5900.00, 2 FROM hotels h
WHERE h.name = 'Marina Bay Hotel'
  AND NOT EXISTS (SELECT 1 FROM rooms r WHERE r.hotel_id = h.id AND r.room_type = 'Suite');
