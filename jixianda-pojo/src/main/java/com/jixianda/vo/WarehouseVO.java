package com.jixianda.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WarehouseVO implements Serializable {

    private Long id;

    private String name;

    private String location;

    private String address;

    private String contactName;

    private String phone;

    private Integer status;
}
