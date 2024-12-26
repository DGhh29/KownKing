package com.dg.schoolhelp.ai.controller;

import com.dg.schoolhelp.ai.utils.DocumentReaderUtils;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.moonshot.MoonshotChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
public class ChatAIController {
    private final OllamaChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final ChatMemory chatMemory = new InMemoryChatMemory();
    private final ZhiPuAiChatModel zhiPuAiChatModel;
    private final MoonshotChatModel moonshotChatModel;



    // 向量数据库
    private final VectorStore vectorStore;


    private final DocumentReaderUtils documentReaderUtils;


    public ChatAIController(OllamaChatModel chatModel, @Qualifier("ollamaEmbeddingModel")EmbeddingModel embeddingModel, ZhiPuAiChatModel zhiPuAiChatModel, MoonshotChatModel moonshotChatModel, VectorStore vectorStore, DocumentReaderUtils documentReaderUtils) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.zhiPuAiChatModel = zhiPuAiChatModel;
        this.moonshotChatModel = moonshotChatModel;
        this.vectorStore = vectorStore;
        this.documentReaderUtils = documentReaderUtils;
    }

    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "你好，你是谁啊，介绍一下你自己呗") String message) {
        ChatClient chatClient = ChatClient.create(moonshotChatModel);
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping(value = "/ai/generateStream")
    public Flux<ServerSentEvent<String>> generateStream(@RequestParam(value = "prompt", defaultValue = "你好，给我讲个笑话吧") String prompt) {
        ChatClient chatClient = ChatClient.create(chatModel);
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .map(chatResponse -> {
                    // 构建 ServerSentEvent
                    return ServerSentEvent.builder(chatResponse).build();
                });
    }

    @GetMapping(value = "chat/stream/history")
    public Flux<ServerSentEvent<String>> chatStreamWithHistory(@RequestParam String prompt,
                                                               @RequestParam String sessionId) {
        // 1. 如果需要存储会话和消息到数据库，自己可以实现ChatMemory接口，
        //    这里使用InMemoryChatMemory，内存存储。
        // 2. 传入会话id，MessageChatMemoryAdvisor会根据会话id去查找消息。
        // 3. 只需要携带最近10条消息
        var messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory, sessionId, 10);
        return ChatClient.create(chatModel).prompt()
                .user(prompt)
                // MessageChatMemoryAdvisor会在消息发送给大模型之前，从ChatMemory中获取会话的历史消息，
                // 然后一起发送给大模型。
                .advisors(messageChatMemoryAdvisor)
                .stream()
                .content()
                .map(chatResponse -> ServerSentEvent.builder(chatResponse)
                        .event("message")
                        .build());
    }

    //这个是嵌入模型的接口，返回的是向量化数据
    @GetMapping("/ai/embedding")
    public Map embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        // 1. 调用嵌入模型，将文本转换为向量
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
        // 支持同时对多个文本进行向量化List<String>
//        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(messages);
        // 2. 将嵌入结果封装为Map返回
        return Map.of("embedding", embeddingResponse);
    }

    /**
     * 嵌入文件
     *
     * @param file 待嵌入的文件
     * @return 是否成功
     */
    @SneakyThrows
    @PostMapping("embedding")
    public Boolean embedding(@RequestParam MultipartFile file) {
        List<Document> splitDocuments = documentReaderUtils.readMultipartFile(file);
        // 存入向量数据库，这个过程会自动调用embeddingModel,将文本变成向量再存入。
        vectorStore.add(splitDocuments);
        return true;
    }

    @SneakyThrows
    @GetMapping(value = "chat/stream/database", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStreamWithDatabase(@RequestParam String prompt) {
        // 1. 使用向量数据库进行检索
        List<Document> retrievedDocuments = vectorStore.similaritySearch(
                SearchRequest.query(prompt).withTopK(3).withSimilarityThreshold(0.5)  // 设置返回top 3相似文档
        );

        // 2. 检查是否检索到文档
        if (retrievedDocuments.isEmpty()) {
            // 如果没有检索到相关文档，直接调用大模型
            return ChatClient.create(chatModel).prompt()
                    .user(prompt)
                    .stream()
                    .content()
                    .map(chatResponse -> ServerSentEvent.builder(chatResponse)
                            .event("message")
                            .build());
        } else {
            // 如果检索到文档，则使用原来的上下文方法
            String promptWithContext = """
            下面是上下文信息
            ---------------------
            {question_answer_context}
            ---------------------
            严格遵守以下规则：
            1. 只能基于提供的上下文回答问题
            2. 如果上下文中没有找到相关信息，必须且只能回复："不知道"
            3. 不要使用其他措辞或尝试猜测
            4. 不要添加任何额外解释
            """;

            return ChatClient.create(chatModel).prompt()
                    .user(prompt)
                    .advisors(new QuestionAnswerAdvisor(vectorStore,
                            SearchRequest.query(prompt).withTopK(3),
                            promptWithContext))
                    .stream()
                    .content()
                    .map(chatResponse -> {
                        // 构建 ServerSentEvent
                        return ServerSentEvent.builder(chatResponse).build();
                    });
        }
    }


}
