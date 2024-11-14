package com.project.backend.services.location;

import com.project.backend.models.Location;
import com.project.backend.responses.ResponseObject;

public interface ILocationService {
    public ResponseObject findAllLocations();
    public ResponseObject findById(Integer id);
    public ResponseObject saveLocation(Location location);
    public ResponseObject deleteLocation(Integer id);

    // New methods for searching
    public ResponseObject findByCampus(String campus);
    public ResponseObject findByCampusAndName(String campus, String name);
    public ResponseObject findByCampusAndNameAndFloor(String campus, String name, Integer floor);
}
