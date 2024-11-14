package com.project.backend.repositories;

import com.project.backend.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    // Find locations by campus
    List<Location> findByCampus(String campus);

    // Find locations by campus and department
    List<Location> findByCampusAndName(String campus, String name);

    // Find locations by campus, department, and floor
    List<Location> findByCampusAndNameAndFloor(String campus, String name, Integer floor);

}
