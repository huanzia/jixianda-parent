package com.jixianda.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 前置仓实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /** 仓库名称 */
    private String name;
    /** 经纬度，例如: 31.2304,121.4737 */
    private String location;
    /** 仓库地址 */
    private String address;
    /** 联系人 */
    private String contactName;
    /** 联系电话 */
    private String phone;
    /** 状态：0-禁用，1-启用 */
    private Integer status;
}
