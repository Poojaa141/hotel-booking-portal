package com.pooja.hotelbooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pooja.hotelbooking.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findFirstByHotelIdOrderByIdAsc(Long hotelId);
}
