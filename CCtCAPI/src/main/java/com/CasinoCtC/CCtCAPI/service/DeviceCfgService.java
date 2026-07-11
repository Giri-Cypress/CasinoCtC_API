package com.CasinoCtC.CCtCAPI.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.CasinoCtC.CCtCAPI.entity.DeviceCfgEntity;
import com.CasinoCtC.CCtCAPI.entity.DeviceCfgId;
import com.CasinoCtC.CCtCAPI.repository.DeviceCfgRepository;

@Service
public class DeviceCfgService {

    private final DeviceCfgRepository repository;

    public DeviceCfgService(DeviceCfgRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DeviceCfgEntity> getAll() {
        return repository.findAllByOrderByLocationNumberAscStationNameAscKindsAsc();
    }

    @Transactional(readOnly = true)
    public List<DeviceCfgEntity> getActiveByLocation(Integer locationNumber) {
        if (locationNumber == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location Number is required");
        }
        return repository.findByLocationNumberAndUseflgOrderByDeviceNameAscStationNameAscKindsAsc(locationNumber, 1);
    }

    @Transactional(readOnly = true)
    public DeviceCfgEntity getById(String stationName, Integer kinds) {
        return repository.findById(new DeviceCfgId(stationName, kinds))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Device configuration not found"));
    }

    @Transactional
    public DeviceCfgEntity save(DeviceCfgEntity request) {
        validate(request);

        request.setStationName(request.getStationName().trim());
        request.setComport(request.getComport().trim());
        request.setModule(request.getModule().trim());
        request.setIp(trimToNull(request.getIp()));
        request.setDeviceName(trimToNull(request.getDeviceName()));
        request.setTicketFtpId(trimToNull(request.getTicketFtpId()));

        return repository.save(request);
    }

    @Transactional
    public void delete(String stationName, Integer kinds) {
        DeviceCfgId id = new DeviceCfgId(stationName, kinds);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Device configuration not found");
        }
        repository.deleteById(id);
    }

    private void validate(DeviceCfgEntity request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Device configuration is required");
        }
        if (isBlank(request.getStationName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Station Name is required");
        }
        if (request.getStationName().trim().length() > 512) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Station Name cannot exceed 512 characters");
        }
        if (request.getLocationNumber() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location Number is required");
        }
        if (request.getKinds() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kinds is required");
        }
        if (isBlank(request.getComport())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Com Port is required");
        }
        if (request.getComport().trim().length() > 25) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Com Port cannot exceed 25 characters");
        }
        if (isBlank(request.getModule())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Module is required");
        }
        if (request.getModule().trim().length() > 25) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Module cannot exceed 25 characters");
        }
        if (request.getUseflg() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use Flag is required");
        }
        validateMaxLength(request.getIp(), 15, "IP");
        validateMaxLength(request.getDeviceName(), 512, "Device Name");
        validateMaxLength(request.getTicketFtpId(), 50, "Ticket FTP ID");
    }

    private void validateMaxLength(String value, int maxLength, String label) {
        if (value != null && value.trim().length() > maxLength) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    label + " cannot exceed " + maxLength + " characters");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        String trimmed = value == null ? null : value.trim();
        return trimmed == null || trimmed.isEmpty() ? null : trimmed;
    }
}
