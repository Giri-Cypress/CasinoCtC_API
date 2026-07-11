package com.CasinoCtC.CCtCAPI.service;

import com.CasinoCtC.CCtCAPI.dto.report.InventoryClearRequest;
import com.CasinoCtC.CCtCAPI.dto.report.InventoryClearResponse;
import com.CasinoCtC.CCtCAPI.entity.InventoryArchiveDtlEntity;
import com.CasinoCtC.CCtCAPI.entity.InventoryArchiveEntity;
import com.CasinoCtC.CCtCAPI.repository.InventoryArchiveDtlRepository;
import com.CasinoCtC.CCtCAPI.repository.InventoryArchiveRepository;
import com.CasinoCtC.CCtCAPI.repository.InventoryClearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryClearService {
    private final InventoryClearRepository inventoryClearRepository;
    private final InventoryArchiveRepository inventoryArchiveRepository;
    private final InventoryArchiveDtlRepository inventoryArchiveDtlRepository;

    public InventoryClearService(
            InventoryClearRepository inventoryClearRepository,
            InventoryArchiveRepository inventoryArchiveRepository,
            InventoryArchiveDtlRepository inventoryArchiveDtlRepository
    ) {
        this.inventoryClearRepository = inventoryClearRepository;
        this.inventoryArchiveRepository = inventoryArchiveRepository;
        this.inventoryArchiveDtlRepository = inventoryArchiveDtlRepository;
    }

    public boolean isClearInventoryEnabled(Integer locationNumber) {
        if (locationNumber == null) {
            return false;
        }
        return inventoryClearRepository.isClearInventoryEnabled(locationNumber);
    }

    @Transactional
    public InventoryClearResponse archiveAndClear(InventoryClearRequest request) {
        List<InventoryClearRepository.InventoryHeaderRow> headers = inventoryClearRepository.findInventoryHeaders(
                request.getLocationNumberFrom(),
                request.getLocationNumberTo(),
                request.getUserNumberFrom(),
                request.getUserNumberTo()
        );

        if (headers.isEmpty()) {
            return new InventoryClearResponse(0, 0, 0, 0, "No inventory found to clear.");
        }

        int archivedHeaderCount = 0;
        int archivedDetailCount = 0;

        for (InventoryClearRepository.InventoryHeaderRow header : headers) {
            InventoryArchiveEntity archive = new InventoryArchiveEntity();
            archive.setLocationNumber(header.getLocationNumber());
            archive.setInvarchDate(LocalDateTime.now());
            archive.setUserNumber(header.getUserNumber());
            archive.setCashAmt(header.getCashAmt());
            archive = inventoryArchiveRepository.saveAndFlush(archive);
            archivedHeaderCount++;

            List<InventoryClearRepository.InventoryDetailRow> details = inventoryClearRepository.findInventoryDetails(
                    header.getLocationNumber(),
                    header.getUserNumber()
            );

            for (InventoryClearRepository.InventoryDetailRow detail : details) {
                InventoryArchiveDtlEntity archiveDtl = new InventoryArchiveDtlEntity();
                archiveDtl.setInvarchNumber(archive.getInvarchNumber());
                archiveDtl.setLocationNumber(detail.getLocationNumber());
                archiveDtl.setDenomNumber(detail.getDenomNumber());
                archiveDtl.setAmount(detail.getAmount());
                inventoryArchiveDtlRepository.save(archiveDtl);
                archivedDetailCount++;
            }
        }

        int clearedDetailCount = inventoryClearRepository.deleteInventoryDetails(
                request.getLocationNumberFrom(),
                request.getLocationNumberTo(),
                request.getUserNumberFrom(),
                request.getUserNumberTo()
        );

        int clearedHeaderCount = inventoryClearRepository.deleteInventoryHeaders(
                request.getLocationNumberFrom(),
                request.getLocationNumberTo(),
                request.getUserNumberFrom(),
                request.getUserNumberTo()
        );

        return new InventoryClearResponse(
                archivedHeaderCount,
                archivedDetailCount,
                clearedHeaderCount,
                clearedDetailCount,
                "Inventory archived and cleared successfully."
        );
    }
}
