package com.torm.sample.entity;

import com.torm.orm.annotation.Column;
import com.torm.orm.annotation.Entity;
import com.torm.orm.annotation.Id;
import com.torm.orm.annotation.IdField;

@Entity(tableName = "role")
@IdField(name = "id")
public class RoleEntity {
    @Id(autoIncrement = false)
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
