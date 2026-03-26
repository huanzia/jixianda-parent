package com.jixianda.service;/*
 *@program:sky-take_out
 *@author: huanzi
 *@Time: 2026/2/6  11:22
 *
 */

import com.jixianda.vo.OrderReportVO;
import com.jixianda.vo.SalesTop10ReportVO;
import com.jixianda.vo.TurnoverReportVO;
import com.jixianda.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单数量统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 查询top10
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /**
     * 数据excel文件导出
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
