package com.dg.schoolhelp.ai.controller;

import com.dg.schoolhelp.ai.entity.AiMessage;
import com.dg.schoolhelp.ai.entity.AiSession;
import com.dg.schoolhelp.ai.service.IAiMessageService;
import com.dg.schoolhelp.ai.service.IAiSessionService;
import com.dg.schoolhelp.ai.service.impl.AiMessageChatMemory;
import com.dg.schoolhelp.utils.RestBean;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.moonshot.MoonshotChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatPracticeController {

    private final OllamaChatModel chatModel;
    private final VectorStore vectorStore;
    private final AiMessageChatMemory chatMemory;
    private final MoonshotChatModel moonshotChatModel;

    @Autowired
    private IAiMessageService aiMessageService;

    @Autowired
    private IAiSessionService aiSessionService;

    public ChatPracticeController(OllamaChatModel chatModel, VectorStore vectorStore, AiMessageChatMemory chatMemory, MoonshotChatModel moonshotChatModel) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.chatMemory = chatMemory;
        this.moonshotChatModel = moonshotChatModel;
    }

    //通过sessionId查询会话记录
    @GetMapping(value = "/aimessage")
    public RestBean sessionBySessionId(String sessionId) {
        try {
            List<AiMessage> aiMessage = aiMessageService.findBySessionId(sessionId);
            return RestBean.success(aiMessage);
        } catch (Exception e) {
            return RestBean.failure(500);
        }
    }

    @GetMapping(value = "/session/id")
    public RestBean sessionBySessionName(String sessionId) {
        try {
            return RestBean.success(aiSessionService.getSessionNameById(sessionId));
        } catch (Exception e) {
           return RestBean.failure(500);
        }


    }

    @GetMapping(value = "/session/delete")
    public RestBean deleteSessionBySessionId(@RequestParam String sessionId) {
        try {
            return RestBean.success(aiSessionService.removeById(sessionId));
        } catch (Exception e) {
            return RestBean.failure(500);
        }

    }

    // 查询数据库中会话记录
    @GetMapping(value = "/session")
    public RestBean session(int userId) {
        try {
            List<AiSession> aiSessions = aiSessionService.getSessionsByUserId(userId);
            if (!aiSessions.isEmpty()) return RestBean.success(aiSessionService.getSessionsByUserId(userId));
            else return RestBean.failure(404);
        } catch (Exception e) {
            return RestBean.failure(500);
        }

    }

    @GetMapping(value = "/practice", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> practice(AiMessage message, boolean knowledgeBaseId, boolean function,String model) {
        try {
            if (message.getAiSessionId() == null || message.getAiSessionId().isEmpty()) {
                String titile = message.getTextContent();
                if (message.getTextContent().length() > 15) {
                    titile = generateTitle(message.getTextContent());
                }
                String sessionId = aiSessionService.insertSession(titile, message.getCreatorId());
                message.setAiSessionId(sessionId);
            }
            message.setId(UUID.randomUUID().toString());
            message.setType("1");
            message.setMedias("[]");
            message.setCreatedTime(LocalDateTime.now());

            aiMessageService.save(message);

            if (Objects.equals(model, "Kown King"))model = "Ollama";
            else model = "moonshot";
            return createStreamWithMessageChatMemoryAdvisor(message,knowledgeBaseId, function,model);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateTitle(String text) {
        String title = "帮我通过下面这个文本总结一个标题：“" + text + "”只要这个返回标题就行，不要超过15个字。不要回复其他内容";
        ChatClient chatClient = ChatClient.create(chatModel);
        return chatClient.prompt()
                .user(title)
                .call()
                .content();
    }

    private Flux<ServerSentEvent<String>> createStreamWithMessageChatMemoryAdvisor(AiMessage message,boolean databases, boolean function, String Model) {
        // 在流反应中收集数据拼接到字符串中
        var messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory, message.getAiSessionId(), 20);
        StringBuffer buffer = new StringBuffer();
        String prompt = message.getTextContent();

        if (Model.equals("Ollama")) {
            // 根据是否需要 function 来构建函数参数
            var chatClientBuilder = ChatClient.create(chatModel).prompt()
                    .user(prompt);

            if (!databases) {
                chatClientBuilder.advisors(messageChatMemoryAdvisor);
            } else {
                // 构建带有上下文的查询
                String promptWithContext = """
        下面是上下文信息
        ---------------------
        {question_answer_context}
        ---------------------
        严格遵守以下规则：
        1. 只能基于提供的上下文回答问题
        2. 如果上下文中没有找到相关信息，必须且只能回复："不知道"
        3. 不要使用其他措辞或尝试猜测-
        4. 不要添加任何额外解释
        """;
                chatClientBuilder.advisors(new QuestionAnswerAdvisor(vectorStore,
                        SearchRequest.builder().similarityThreshold(0.8d).topK(6).build(),
                        promptWithContext),messageChatMemoryAdvisor);
            }

            // 如果需要，则加入 function
            if (function) {
                chatClientBuilder.functions("documentAnalyzerFunction");
            }

            return chatClientBuilder.stream()

                    .content()
                    .doOnNext(buffer::append)
                    .doOnComplete(() -> {
                        // 流结束时保存数据到数据库
                        message.setId(UUID.randomUUID().toString());
                        message.setCreatedTime(LocalDateTime.now());
                        message.setType("2");
                        message.setTextContent(String.valueOf(buffer));
                        aiMessageService.save(message);
                    })
                    .map(chatResponse ->
                            ServerSentEvent.builder(chatResponse)
                                    .id(message.getAiSessionId())  // 可以在 ServerSentEvent 中也携带 sessionID
                                    .build()
                    );
        }else {
            // 根据是否需要 function 来构建函数参数
            var chatClientBuilder = ChatClient.create(moonshotChatModel).prompt()
                    .user(prompt);

            if (!databases) {
                chatClientBuilder.advisors(messageChatMemoryAdvisor);
            } else {
                // 构建带有上下文的查询
                String promptWithContext = """
        下面是上下文信息
        ---------------------
        {question_answer_context}
        ---------------------
        严格遵守以下规则：
        1. 只能基于提供的上下文回答问题
        2. 如果上下文中没有找到相关信息，必须且只能回复："不知道"
        3. 不要使用其他措辞或尝试猜测-
        4. 不要添加任何额外解释
        """;
                chatClientBuilder.advisors(new QuestionAnswerAdvisor(vectorStore,
                        SearchRequest.builder().similarityThreshold(0.8d).topK(6).build(),
                        promptWithContext),messageChatMemoryAdvisor);
            }

            // 如果需要，则加入 function
            if (function) {
                chatClientBuilder.functions("documentAnalyzerFunction");
            }

            return chatClientBuilder.stream()

                    .content()
                    .doOnNext(buffer::append)
                    .doOnComplete(() -> {
                        // 流结束时保存数据到数据库
                        message.setId(UUID.randomUUID().toString());
                        message.setCreatedTime(LocalDateTime.now());
                        message.setType("2");
                        message.setTextContent(String.valueOf(buffer));
                        aiMessageService.save(message);
                    })
                    .map(chatResponse ->
                            ServerSentEvent.builder(chatResponse)
                                    .id(message.getAiSessionId())  // 可以在 ServerSentEvent 中也携带 sessionID
                                    .build()
                    );
        }

    }
}
