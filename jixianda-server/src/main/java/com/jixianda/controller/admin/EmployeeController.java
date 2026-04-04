package com.jixianda.controller.admin;

import com.jixianda.constant.JwtClaimsConstant;
import com.jixianda.dto.EmployeeDTO;
import com.jixianda.dto.EmployeeLoginDTO;
import com.jixianda.dto.EmployeePageQueryDTO;
import com.jixianda.entity.Employee;
import com.jixianda.properties.JwtProperties;
import com.jixianda.result.PageResult;
import com.jixianda.result.Result;
import com.jixianda.service.EmployeeService;
import com.jixianda.utils.JwtUtil;
import com.jixianda.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工登出")
    public Result<String> logout() {
        log.info("员工登出");
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @ApiOperation("员工分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为对象：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启动禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启动禁用员工账号")
    public Result changeStatus(@PathVariable Integer status,Long id){
        log.info("启用禁用员工账号，{}，{}",status,id);
        employeeService.changeStatus(status,id);
        return Result.success();
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @ApiOperation("根据id查询员工")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息,{}",id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }


    /**
     * 前端提交过来的数据是json数据，需要使用@RequestBody
     * 由于这里只是编辑员工信息，并不需要给前端返回什么数据，所以泛型可以不声明
     * 修改员工信息
     * @param employeeDTO
     * @return
     */
    @ApiOperation("编辑员工信息")
    @PutMapping
    public Result updateEmployeeInfomation(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工信息,{}",employeeDTO);
        employeeService.updateEmployeeInfomation(employeeDTO);
        return Result.success();
    }

}
