package com.CasinoCtC.CCtCAPI.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CasinoCtC.CCtCAPI.model.CollectionPoint;
import com.CasinoCtC.CCtCAPI.service.CollectionPointService;

@RestController
@RequestMapping("/api/collection-points")
@PreAuthorize("hasRole('ADMIN')")
public class CollectionPointController {

    private final CollectionPointService service;

    public CollectionPointController(
            CollectionPointService service) {

        this.service = service;
    }

    @GetMapping
    public List<CollectionPoint>
            getAllCollectionPoints() {

        return service.getAllCollectionPoints();
    }

    @GetMapping("/{collectionPointId}")
    public CollectionPoint
            getCollectionPoint(
                    @PathVariable
                    Integer collectionPointId) {

        return service.getCollectionPoint(
                collectionPointId);
    }

    @PostMapping
    public CollectionPoint saveCollectionPoint(
            @RequestBody CollectionPoint model) {

        return service.saveCollectionPoint(model);
    }

    @DeleteMapping("/{collectionPointId}")
    public void deleteCollectionPoint(
            @PathVariable
            Integer collectionPointId) {

        service.deleteCollectionPoint(
                collectionPointId);
    }
}