package com.jixianda.controller.admin;/*
 *@program:sky-take_out
 *@author: huanzi
 *@Time: 2026/2/6  11:18
 *
 */

import com.jixianda.result.Result;
import com.jixianda.service.ReportService;
import com.jixianda.vo.OrderReportVO;
import com.jixianda.vo.SalesTop10ReportVO;
import com.jixianda.vo.TurnoverReportVO;
import com.jixianda.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Api(tags = "营业额统计相关接口")
@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> queryTurnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额统计,{},{}",begin,end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }
    /**
     * 用户数量统计
     * @param begin
     * @param end
     * @return
     */

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> queryOrdersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户统计,{},{}",begin,end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    /**
     * 订单数量统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数量统计")
    public Result<OrderReportVO> getOrdersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单数量统计,{},{}",begin,end);
        return Result.success(reportService.getOrdersStatistics(begin, end));
    }

    /**
     * 查询top10
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("查询top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("查询top10,{},{}",begin,end);
        return Result.success(reportService.getSalesTop10(begin,end));
    }

    /**
     * 数据excel文件导出
     * @param response
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        log.info("导出数据");
        reportService.exportBusinessData(response);
    }
}
