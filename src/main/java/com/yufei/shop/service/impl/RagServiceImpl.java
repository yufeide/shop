package com.yufei.shop.service.impl;

import com.yufei.shop.entity.Result;
import com.yufei.shop.service.RagService;
import com.yufei.shop.service.RagStreamService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.BgeSmallZhEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class RagServiceImpl {

    private final EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    private final EmbeddingModel embeddingModel = new BgeSmallZhEmbeddingModel();
    private final OllamaChatModel chatModel;
    private final OllamaStreamingChatModel streamingChatModel;

    private RagService ragAssistant;
    private RagStreamService ragStreamAssistant;

    public RagServiceImpl(OllamaChatModel chatModel, OllamaStreamingChatModel streamingChatModel) {
        this.chatModel = chatModel;
        this.streamingChatModel = streamingChatModel;
    }

    @PostConstruct
    public void init() {
        ragAssistant = AiServices.builder(RagService.class)
                .chatLanguageModel(chatModel)
                .retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel))
                .build();
        
        ragStreamAssistant = AiServices.builder(RagStreamService.class)
                .streamingChatLanguageModel(streamingChatModel)
                .retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel))
                .build();
        
        log.info("RAG助手初始化完成");
    }

    public String uploadFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", ".pdf");
        file.transferTo(tempFile);

        String text = "";
        try (PDDocument document = PDDocument.load(tempFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (Exception e) {
            log.error("PDF解析失败", e);
            throw new IOException("PDF解析失败", e);
        }

        Document document = Document.from(text);

        DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
        List<TextSegment> segments = splitter.split(document);

        embeddingStore.addAll(embeddingModel.embedAll(segments).content(), segments);

        tempFile.deleteOnExit();
        log.info("PDF上传成功，知识库构建完成，共{}个文档片段", segments.size());
        return "PDF上传成功，知识库构建完成";
    }

    public String ask(String question) {
        if (ragAssistant == null) {
            throw new IllegalStateException("RAG助手未初始化");
        }
        return ragAssistant.answer(question);
    }

    public Flux<String> streamAnswer(String question) {
        if (ragStreamAssistant == null) {
            throw new IllegalStateException("RAG流式助手未初始化");
        }
        
        TokenStream tokenStream = ragStreamAssistant.answerStream(question);
        log.info("执行到这里了");
        
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();
            tokenStream
                    .onNext(token -> {
                        buffer.append(token);
                        if (buffer.length() >= 10 || token.contains("\n") || token.contains("。") || token.contains("，")) {
                            sink.next(buffer.toString());
                            buffer.setLength(0);
                        }
                    })
                    .onComplete(response -> {
                        if (buffer.length() > 0) {
                            sink.next(buffer.toString());
                        }
                        sink.complete();
                    })
                    .onError(sink::error)
                    .start();
        });
    }
}
