package com.jixianda.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jixianda.constant.MessageConstant;
import com.jixianda.constant.PasswordConstant;
import com.jixianda.constant.StatusConstant;
import com.jixianda.dto.EmployeeDTO;
import com.jixianda.dto.EmployeeLoginDTO;
import com.jixianda.dto.EmployeePageQueryDTO;
import com.jixianda.entity.Employee;
import com.jixianda.exception.AccountLockedException;
import com.jixianda.exception.AccountNotFoundException;
import com.jixianda.exception.PasswordErrorException;
import com.jixianda.mapper.EmployeeMapper;
import com.jixianda.result.PageResult;
import com.jixianda.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {


    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端请求过来的明文进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //EmployeeDTO类里面拥有的属性，Employee类里面全都有
        //属性拷贝，前面拷贝到后面
        BeanUtils.copyProperties(employeeDTO,employee);

        //设置员工账号的默认状态，1正常，0锁定
        employee.setStatus(StatusConstant.ENABLE);

        //设置员工账号的默认密码，使用md5加密进行存储
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

//        //设置员工账号的创建时间
//        //设置更新员工账号的时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //设置当前记录创建人id和修改人id
////        employee.setCreateUser(10L);
////        employee.setUpdateUser(10L);
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
//        select * from employee limit 0,10
        /**
         * @param employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize()
         * 页码，页数
         */
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> result = page.getResult();
        return new PageResult(total,result);
    }

    /**
     * 启动禁用员工账号
     * @param status
     * @param id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("*****");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void updateEmployeeInfomation(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

        employeeMapper.update(employee);
    }

    @Override
    public Long getWarehouseIdById(Long id) {
        if (id == null) {
            return null;
        }
        Employee employee = employeeMapper.getById(id);
        return employee == null ? null : employee.getWarehouseId();
    }
}
