DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS hotels;

CREATE TABLE hotels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    amenities VARCHAR(500)
);

CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL CHECK (price_per_night > 0),
    total_rooms INT NOT NULL CHECK (total_rooms > 0),
    FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_ref VARCHAR(20) NOT NULL UNIQUE,
    room_id BIGINT NOT NULL,
    guest_name VARCHAR(100) NOT NULL,
    guest_email VARCHAR(150) NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    nights INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('CONFIRMED', 'CANCELLED')),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    CHECK (check_out > check_in)
);

CREATE INDEX idx_bookings_room_dates ON bookings (room_id, check_in, check_out);
