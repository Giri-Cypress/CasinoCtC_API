package com.CasinoCtC.CCtCAPI.dao;

import java.util.List;
import com.CasinoCtC.CCtCAPI.model.LocationMisc;

public interface LocationMiscDAO {

    List<LocationMisc> findByLocationId(int locationId);

    void insertAll(List<LocationMisc> list);

    void deleteByLocationId(int locationId);
}