package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.CasinoCtC.CCtCAPI.dto.DenominationDto;
import com.CasinoCtC.CCtCAPI.entity.DenominationEntity;
import com.CasinoCtC.CCtCAPI.repository.DenominationRepository;

@Service
public class DenominationService {
    private final DenominationRepository denominationRepository;

    public DenominationService(DenominationRepository denominationRepository) {
        this.denominationRepository = denominationRepository;
    }

    public List<DenominationDto> getActiveDenominations() {
        return denominationRepository
                .findByActiveOrderByDisplayOrderAscDenomNumberAsc(1)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private DenominationDto toDto(DenominationEntity entity) {
        DenominationDto dto = new DenominationDto();
        dto.setDenomNumber(entity.getDenomNumber());
        dto.setDenomValue(entity.getDenomValue());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        dto.setDisplayOrder(entity.getDisplayOrder());
        return dto;
    }
}
