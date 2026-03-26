package com.yufei.shop.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yufei.shop.service.OllamaService;
import com.yufei.shop.service.SkillService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class OllamaServiceImpl implements OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SkillService skillService;


    @Override
    public String sendToOllama(String userMessage) throws Exception{
        String url = "http://localhost:11434/api/chat";

        // 构建工具定义
        Map<String, Object> tool = new HashMap<>();
        tool.put("type", "function");
        Map<String, Object> func = new HashMap<>();
        func.put("name", "calculator");
        func.put("description", "计算数学表达式");
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        Map<String, String> exprProp = new HashMap<>();
        exprProp.put("type", "string");
        exprProp.put("description", "数学表达式");
        properties.put("expression", exprProp);
        params.put("properties", properties);
        params.put("required", Collections.singletonList("expression"));
        func.put("parameters", params);
        tool.put("function", func);

        // 构建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("model", "qwen3.5:0.8b");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        body.put("messages", messages);
        body.put("stream", false);
        body.put("tools", Collections.singletonList(tool));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());

        log.info("root的内容：{}",root.toString());

        // 检查模型是否生成 tool_call
        JsonNode toolCalls = root.at("/message/tool_calls");
        if (toolCalls.isArray() && toolCalls.size() > 0) {
            JsonNode call = toolCalls.get(0);
            String expression = call.at("/function/arguments/expression").asText();

            // 调用 Python Skill
            log.info("开始调用pythonSkillApi");
            String skillResult = skillService.callCalculator(expression);

            return "计算结果: " + skillResult;
        }

        // 没有调用工具则返回模型普通回答
        log.info("content的内容{}",root.at("/message/content").asText());
        return root.at("/message/content").asText();
    }
}
