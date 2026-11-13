ALTER TABLE hotels ADD COLUMN image_url VARCHAR(255) NULL;

UPDATE hotels SET image_url = '/images/hotels/seabreeze-palm-resort.jpg' WHERE name = 'Seabreeze Palm Resort';
UPDATE hotels SET image_url = '/images/hotels/coconut-grove-inn.jpg' WHERE name = 'Coconut Grove Inn';
UPDATE hotels SET image_url = '/images/hotels/harbor-view-suites.jpg' WHERE name = 'Harbor View Suites';
UPDATE hotels SET image_url = '/images/hotels/capital-courtyard-hotel.jpg' WHERE name = 'Capital Courtyard Hotel';
UPDATE hotels SET image_url = '/images/hotels/amber-fort-residency.jpg' WHERE name = 'Amber Fort Residency';
UPDATE hotels SET image_url = '/images/hotels/garden-city-stay.jpg' WHERE name = 'Garden City Stay';

INSERT INTO hotels (name, location, description, amenities, image_url) VALUES
    ('Lake Palace Retreat', 'Udaipur', 'A lakeside heritage stay with sunset views across the water.', 'WiFi, Pool, Spa, Breakfast', '/images/hotels/lake-palace-retreat.jpg'),
    ('Backwater Haven', 'Kochi', 'A quiet stay beside the backwaters with calm views of the waterways.', 'WiFi, Breakfast, Parking, Pool', '/images/hotels/backwater-haven.jpg'),
    ('Pine Valley Lodge', 'Manali', 'A wooden lodge in the pine forest near the snow line.', 'WiFi, Fireplace, Breakfast, Parking', '/images/hotels/pine-valley-lodge.jpg'),
    ('Ganga View Resort', 'Rishikesh', 'A riverside resort with peaceful yoga mornings by the Ganga.', 'WiFi, Yoga, Breakfast, Parking', '/images/hotels/ganga-view-resort.jpg'),
    ('Deccan Heights Hotel', 'Hyderabad', 'A modern business hotel with wide skyline views.', 'WiFi, Gym, Restaurant, Parking', '/images/hotels/deccan-heights-hotel.jpg'),
    ('Marina Bay Hotel', 'Chennai', 'A coastal hotel a short walk from the beach.', 'WiFi, Pool, Gym, Breakfast', '/images/hotels/marina-bay-hotel.jpg');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT id, 'Deluxe', 5200.00, 4 FROM hotels WHERE name = 'Lake Palace Retreat';

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT id, 'Deluxe', 3600.00, 5 FROM hotels WHERE name = 'Backwater Haven';

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT id, 'Deluxe', 4100.00, 3 FROM hotels WHERE name = 'Pine Valley Lodge';

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT id, 'Deluxe', 3400.00, 4 FROM hotels WHERE name = 'Ganga View Resort';

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT id, 'Deluxe', 3900.00, 6 FROM hotels WHERE name = 'Deccan Heights Hotel';

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms)
SELECT id, 'Deluxe', 4300.00, 4 FROM hotels WHERE name = 'Marina Bay Hotel';
