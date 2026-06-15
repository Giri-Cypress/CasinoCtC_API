package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import com.CasinoCtC.CCtCAPI.model.LocationMisc;

public interface LocationMiscService {

    List<LocationMisc> getByLocationId(int locationId);

    void saveLocationMisc(int locationId, List<LocationMisc> list);
}
