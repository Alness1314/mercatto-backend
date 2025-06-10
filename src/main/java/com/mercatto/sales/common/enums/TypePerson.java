package com.mercatto.sales.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypePerson {
    MORAL(1, "Moral"),
    FISICA(2, "Física");

    private Integer id;
    private String name;
}
