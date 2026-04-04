package com.jixianda.mapper;

import com.github.pagehelper.Page;
import com.jixianda.annotation.AutoFill;
import com.jixianda.dto.EmployeePageQueryDTO;
import com.jixianda.entity.Employee;
import com.jixianda.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @AutoFill(value = OperationType.INSERT)
    void insert(Employee employee);

    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}