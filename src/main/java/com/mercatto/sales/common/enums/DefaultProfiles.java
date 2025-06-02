package com.mercatto.sales.common.enums;

import lombok.Getter;

@Getter
public enum DefaultProfiles {
    SADMIN("Master", 0),
    ADMIN("Administrador", 1), 
    EMPLOYEE("Empleado", 2);


    private String name;
    private Integer priority;

    DefaultProfiles(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }

}
