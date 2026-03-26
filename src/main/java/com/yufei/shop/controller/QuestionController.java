package com.yufei.shop.controller;

import com.yufei.shop.annotation.TotalTime;
import com.yufei.shop.entity.Result;
import com.yufei.shop.service.impl.RagServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;


@Slf4j
@Api(tags = "查询知识库助手", description = "这个请求前缀下的接口，用于进行rag检索增强")
@RestController
@RequestMapping("/api/rag")
@AllArgsConstructor
public class QuestionController {

    private final RagServiceImpl ragService;

    @ApiOperation("上传PDF文件构建知识库")
    @PostMapping("/upload")
    public Result<String> uploadFile(@ApiParam("PDF文件") @RequestParam("file") MultipartFile file) {
        try {
            String result = ragService.uploadFile(file);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }

    @ApiOperation("普通向知识库提问接口，等答案全部生成再返回")
    @GetMapping("/ask")
    public Result<String> ask(@ApiParam("问题") @RequestParam("question") String question) {
        try {
            String answer = ragService.ask(question);
            return Result.success(answer);
        } catch (Exception e) {
            return Result.fail("提问失败: " + e.getMessage());
        }
    }

    @TotalTime
    @ApiOperation("普通向知识库提问接口，流式返回")
    @GetMapping(value = "/stream/ask",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamAsk(@RequestParam("question") String question){
        log.info("接收到了请求");
        return ragService.streamAnswer(question);
    }

}
