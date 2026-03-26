package com.jixianda.service;

import com.jixianda.dto.EmployeeDTO;
import com.jixianda.dto.EmployeeLoginDTO;
import com.jixianda.dto.EmployeePageQueryDTO;
import com.jixianda.entity.Employee;
import com.jixianda.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */

    void save(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启动禁用员工账号
     * @param status
     * @param id
     */
    void changeStatus(Integer status, Long id);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     */

    void updateEmployeeInfomation(EmployeeDTO employeeDTO);

    /**
     * Query employee warehouse id by employee id.
     */
    Long getWarehouseIdById(Long id);
}
