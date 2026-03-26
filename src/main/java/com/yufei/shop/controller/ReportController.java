package com.yufei.shop.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yufei.shop.annotation.TotalTime;
import com.yufei.shop.entity.ApiCallRecord;
import com.yufei.shop.entity.Result;
import com.yufei.shop.mapper.ApiCallRecordMapper;
import com.yufei.shop.service.ExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Api(tags = "报表导出接口", description = "导出接口调用统计报表")
@RestController
@RequestMapping("/api/report")
@AllArgsConstructor
public class ReportController {

    private final ApiCallRecordMapper apiCallRecordMapper;
    private final ExportService exportService;

    @ApiOperation("导出接口调用统计报表")
    @GetMapping("/export")
    public Result<String> exportReport(HttpServletResponse response) {
        return Result.success(exportService.getExcel());
    }

    @ApiOperation("获取接口调用统计数据")
    @GetMapping("/statistics")
    @TotalTime("获取接口调用统计数据")
    public Result<List<ApiCallRecord>> getStatistics(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {

        LambdaQueryWrapper<ApiCallRecord> wrapper = new LambdaQueryWrapper<>();

        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(ApiCallRecord::getCallTime, LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(ApiCallRecord::getCallTime, LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        wrapper.orderByDesc(ApiCallRecord::getCallTime);

        List<ApiCallRecord> records = apiCallRecordMapper.selectList(wrapper);
        return Result.success(records);
    }
}
