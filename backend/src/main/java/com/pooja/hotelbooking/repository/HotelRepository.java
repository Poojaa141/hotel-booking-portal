package com.pooja.hotelbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pooja.hotelbooking.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findAllByLocationContainingIgnoreCase(String location);
}
