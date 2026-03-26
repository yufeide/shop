package com.yufei.shop.service;


import org.springframework.stereotype.Service;

@Service
public interface OllamaService {

    String sendToOllama(String userMessage) throws Exception;
}
