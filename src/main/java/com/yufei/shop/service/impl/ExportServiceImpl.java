package com.yufei.shop.service.impl;


import com.yufei.shop.service.ExportService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@AllArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final RestTemplate restTemplate;


    @Override
    public String getExcel() {
        String url = "http://localhost:8001/skill/export_to_excel";

        //发起请求
        Map response = restTemplate.getForObject(url,Map.class);

        if (response.containsKey("result")) {
            return response.get("result").toString();
        } else if (response.containsKey("error")) {
            return "错误: " + response.get("error");
        }
        return "未知错误";
    }
}
