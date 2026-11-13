INSERT INTO hotels (name, location, description, amenities) VALUES
    ('Seabreeze Palm Resort', 'Goa', 'A relaxed beachside resort with easy access to the shore.', 'WiFi, Pool, Breakfast, Parking'),
    ('Coconut Grove Inn', 'Goa', 'A cozy inn offering a quiet stay near local markets.', 'WiFi, Breakfast, Parking'),
    ('Harbor View Suites', 'Mumbai', 'A modern hotel with comfortable rooms and sweeping harbor views.', 'WiFi, Gym, Breakfast, Parking'),
    ('Capital Courtyard Hotel', 'Delhi', 'A welcoming city hotel close to major business and cultural districts.', 'WiFi, Restaurant, Gym, Parking'),
    ('Amber Fort Residency', 'Jaipur', 'A traditional-style retreat located near Jaipur historic sights.', 'WiFi, Pool, Breakfast, Parking'),
    ('Garden City Stay', 'Bengaluru', 'A contemporary hotel offering a peaceful base in the garden city.', 'WiFi, Breakfast, Gym, Parking');

INSERT INTO rooms (hotel_id, room_type, price_per_night, total_rooms) VALUES
    (1, 'Deluxe', 4500.00, 5),
    (2, 'Standard', 2800.00, 3),
    (3, 'Deluxe', 6500.00, 4),
    (4, 'Standard', 3200.00, 6),
    (5, 'Deluxe', 3800.00, 1),
    (6, 'Standard', 3000.00, 5);
