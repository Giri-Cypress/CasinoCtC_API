package com.CasinoCtC.CCtCAPI.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_roles", schema = "gsi")
public class UserRoleEntity {

    @EmbeddedId
    private UserRoleId id;

    public UserRoleEntity() {
    }

    public UserRoleEntity(UserRoleId id) {
        this.id = id;
    }

    public UserRoleId getId() {
        return id;
    }

    public void setId(UserRoleId id) {
        this.id = id;
    }

    public Integer getUserNumber() {
        return id != null ? id.getUserNumber() : null;
    }

    public Integer getRoleNumber() {
        return id != null ? id.getRoleNumber() : null;
    }
}
