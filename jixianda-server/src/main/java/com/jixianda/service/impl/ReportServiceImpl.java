package com.jixianda.service.impl;

import com.jixianda.dto.GoodsSalesDTO;
import com.jixianda.entity.Orders;
import com.jixianda.mapper.OrderMapper;
import com.jixianda.result.Result;
import com.jixianda.client.UserClient;
import com.jixianda.service.ReportService;
import com.jixianda.service.WorkspaceService;
import com.jixianda.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserClient userClient;
    @Autowired
    private WorkspaceService workspaceService;
    
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }


        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>() ;
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double price = orderMapper.sumByMap(map);
            price = (price == null) ? 0.0 : price;
            turnoverList.add(price);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("end",endTime);
            Integer TotalUserNumbers = fetchUserCount(null, endTime);
            totalUserList.add(TotalUserNumbers);
            Integer newAddUserNumbers = fetchUserCount(beginTime, endTime);
            newUserList.add(newAddUserNumbers);

        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        ArrayList<Integer> orderCountList = new ArrayList<>();
        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer countDailyOrder = countOrder(beginTime, endTime, null);
            Integer countDailyValidOrder = countOrder(beginTime, endTime, Orders.COMPLETED);
            orderCountList.add(countDailyOrder);
            validOrderCountList.add(countDailyValidOrder);
        }
        Integer totalNums = orderCountList.stream().reduce(Integer::sum).get();
        Integer validNums = validOrderCountList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0;
        if (totalNums != 0) {
            orderCompletionRate = validNums.doubleValue() / totalNums;
        }


        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalNums)
                .validOrderCount(validNums)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }


    
    private Integer countOrder(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }

    
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> list = orderMapper.getSalesTop(beginTime, endTime);
        List<String> nameList = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameListString = StringUtils.join(nameList, ",");
        List<Integer> numberList = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberListString = StringUtils.join(numberList, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameListString)
                .numberList(numberListString)
                .build();

    }

    
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/\u8fd0\u8425\u6570\u636e\u62a5\u8868\u6a21\u677f.xlsx");

        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("time: " + dateBegin + " to " + dateEnd);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Integer fetchUserCount(LocalDateTime begin, LocalDateTime end) {
        Result<Integer> result = userClient.countUserByTime(begin, end);
        if (result == null || result.getCode() == null || result.getCode() != 1 || result.getData() == null) {
            return 0;
        }
        return result.getData();
    }
}
