package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.entity.CollectionPointEntity;
import com.CasinoCtC.CCtCAPI.model.CollectionPoint;
import com.CasinoCtC.CCtCAPI.repository.CollectionPointRepository;

@Service
public class CollectionPointService {

    private final CollectionPointRepository repository;

    public CollectionPointService(
            CollectionPointRepository repository) {

        this.repository = repository;
    }

    public List<CollectionPoint> getAllCollectionPoints() {

        return repository
                .findAllByOrderByCollectionPointIdAsc()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public CollectionPoint getCollectionPoint(
            Integer collectionPointId) {

        return repository
                .findById(collectionPointId)
                .map(this::toModel)
                .orElse(null);
    }

    public CollectionPoint saveCollectionPoint(
            CollectionPoint model) {

        CollectionPointEntity entity =
                repository.save(
                        toEntity(model));

        return toModel(entity);
    }

    public void deleteCollectionPoint(
            Integer collectionPointId) {

        repository.deleteById(
                collectionPointId);
    }

    private CollectionPoint toModel(
            CollectionPointEntity entity) {

        CollectionPoint model =
                new CollectionPoint();

        model.setCollectionPointId(
                entity.getCollectionPointId());

        model.setLocationNumber(
                entity.getLocationNumber());

        model.setCollectionPointName(
                entity.getCollectionPointName());

        model.setDescription(
                entity.getDescription());

        model.setStatus(
                entity.getStatus());

        return model;
    }

    private CollectionPointEntity toEntity(
            CollectionPoint model) {

        CollectionPointEntity entity =
                new CollectionPointEntity();

        entity.setCollectionPointId(
                model.getCollectionPointId());

        entity.setLocationNumber(
                model.getLocationNumber());

        entity.setCollectionPointName(
                model.getCollectionPointName());

        entity.setDescription(
                model.getDescription());

        entity.setStatus(
                model.getStatus() == null
                        ? 1
                        : model.getStatus());

        return entity;
    }
}