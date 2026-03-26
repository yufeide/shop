package com.yufei.shop.service.impl;

import com.yufei.shop.service.SkillService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
@AllArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final RestTemplate restTemplate;

    @Override
    public String callCalculator(String expression) {
        String url = "http://localhost:8001/skill/calculator";

        Map<String, String> request = new HashMap<>();
        request.put("expression", expression);

        //构造请求
        Map response = restTemplate.postForObject(url, request, Map.class);

        if (response.containsKey("result")) {
            return response.get("result").toString();
        } else if (response.containsKey("error")) {
            return "错误: " + response.get("error");
        }
        return "未知错误";
    }
}
