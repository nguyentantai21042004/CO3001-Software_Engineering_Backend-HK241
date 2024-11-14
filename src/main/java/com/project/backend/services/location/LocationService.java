package com.project.backend.services.location;

import com.project.backend.exceptions.DataNotFoundException;
import com.project.backend.models.Location;
import com.project.backend.repositories.LocationRepository;
import com.project.backend.responses.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService implements ILocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public ResponseObject findAllLocations() {
        ResponseObject response = new ResponseObject();
        try {
            List<Location> locations = locationRepository.findAll();
            response.setData(locations);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Fetched all locations successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject findById(Integer id) {
        ResponseObject response = new ResponseObject();
        try {
            Optional<Location> location = locationRepository.findById(id);
            if (location.isEmpty()) {
                throw new DataNotFoundException("Location not found");
            }
            response.setData(location.get());
            response.setStatus(HttpStatus.OK);
            response.setMessage("Location found successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject saveLocation(Location location) {
        ResponseObject response = new ResponseObject();
        try {
            locationRepository.save(location);
            response.setStatus(HttpStatus.CREATED);
            response.setMessage("Location saved successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject deleteLocation(Integer id) {
        ResponseObject response = new ResponseObject();
        try {
            if (!locationRepository.existsById(id)) {
                throw new DataNotFoundException("Location not found");
            }
            locationRepository.deleteById(id);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Location deleted successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // Search by Campus
    @Override
    public ResponseObject findByCampus(String campus) {
        ResponseObject response = new ResponseObject();
        try {
            List<Location> locations = locationRepository.findByCampus(campus);
            response.setData(locations);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Fetched locations by campus successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // Search by Campus and Department
    @Override
    public ResponseObject findByCampusAndName(String campus, String name) {
        ResponseObject response = new ResponseObject();
        try {
            List<Location> locations = locationRepository.findByCampusAndName(campus, name);
            response.setData(locations);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Fetched locations by campus and department successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    // Search by Campus, Department, and Floor
    @Override
    public ResponseObject findByCampusAndNameAndFloor(String campus, String name, Integer floor) {
        ResponseObject response = new ResponseObject();
        try {
            List<Location> locations = locationRepository.findByCampusAndNameAndFloor(campus, name, floor);
            response.setData(locations);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Fetched locations by campus, department, and floor successfully");
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

}
