package com.yufei.shop.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.TokenStream;

public interface RagStreamService {
    @SystemMessage("你是一个专业的助手，请基于提供的知识库信息回答用户问题。")
    TokenStream answerStream(@UserMessage String question);
}
