package com.jixianda.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WarehousePageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;
}

