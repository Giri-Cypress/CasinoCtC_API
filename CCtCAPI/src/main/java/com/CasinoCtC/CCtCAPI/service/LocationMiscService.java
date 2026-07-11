package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CasinoCtC.CCtCAPI.entity.LocationMiscEntity;
import com.CasinoCtC.CCtCAPI.entity.LocationMiscId;
import com.CasinoCtC.CCtCAPI.model.LocationMisc;
import com.CasinoCtC.CCtCAPI.repository.LocationMiscRepository;

@Service
public class LocationMiscService {

    private final LocationMiscRepository locationMiscRepository;

    public LocationMiscService(LocationMiscRepository locationMiscRepository) {
        this.locationMiscRepository = locationMiscRepository;
    }

    public List<LocationMisc> getByLocationNumber(Integer locationNumber) {
        return locationMiscRepository.findByIdLocationNumberOrderByIdMiscNumberAsc(locationNumber)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Transactional
    public void saveLocationMisc(Integer locationNumber, List<LocationMisc> list) {
        locationMiscRepository.deleteByIdLocationNumber(locationNumber);
        if (list == null || list.isEmpty()) return;
        locationMiscRepository.saveAll(list.stream().map(m -> toEntity(locationNumber, m)).collect(Collectors.toList()));
    }

    @Transactional
    public void deleteLocationMisc(Integer locationNumber) {
        locationMiscRepository.deleteByIdLocationNumber(locationNumber);
    }

    private LocationMisc toModel(LocationMiscEntity entity) {
        LocationMisc model = new LocationMisc();
        model.setLocationNumber(entity.getLocationNumber());
        model.setMiscNumber(entity.getMiscNumber());
        model.setMiscName(entity.getMiscName());
        model.setMiscValue(entity.getMiscValue());
        return model;
    }

    private LocationMiscEntity toEntity(Integer locationNumber, LocationMisc model) {
        LocationMiscEntity entity = new LocationMiscEntity();
        entity.setId(new LocationMiscId(locationNumber, model.getMiscNumber()));
        entity.setMiscName(model.getMiscName());
        entity.setMiscValue(model.getMiscValue());
        return entity;
    }
}
