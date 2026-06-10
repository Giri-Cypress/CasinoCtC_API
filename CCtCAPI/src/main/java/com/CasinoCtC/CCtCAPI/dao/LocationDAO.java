package com.CasinoCtC.CCtCAPI.dao;

import java.util.Optional;

import com.CasinoCtC.CCtCAPI.model.Location;

public interface LocationDAO {

	Optional<Location> findById(Integer locationId);

}
