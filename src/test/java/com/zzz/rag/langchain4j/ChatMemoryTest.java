package com.zzz.rag.langchain4j;

import com.zzz.rag.langchain4j.assistant.Assistant;
import com.zzz.rag.langchain4j.assistant.MemoryChatAssistant;
import com.zzz.rag.langchain4j.assistant.SeparateChatAssistant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatMemoryTest {

    @Autowired
    private Assistant assistant;

    @Autowired
    QwenChatModel qwenChatModel;

    @Test
    public void testChatMemory() {

        //创建chatMemory
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        //创建AIService
        Assistant assistant = AiServices
                .builder(Assistant.class)
                .chatLanguageModel(qwenChatModel)
                .chatMemory(chatMemory)
                .build();

        String answer1 = assistant.chat("我是yudy");
        System.out.println(answer1);

        String answer2 = assistant.chat("我是谁");
        System.out.println(answer2);
    }

    @Autowired
    private MemoryChatAssistant memoryChatAssistant;

    @Test
    public void testChatMemory4() {
        String answer1 = memoryChatAssistant.chat("我是李白");
        System.out.println(answer1);
        String answer2 = memoryChatAssistant.chat("我是谁");
        System.out.println(answer2);
    }

    @Autowired
    private SeparateChatAssistant separateChatAssistant;

    @Test
    public void testChatMemory5() {
        String answer1 = separateChatAssistant.chat(1, "我是yudy");
        System.out.println(answer1);
        String answer2 = separateChatAssistant.chat(1, "我是谁");
        System.out.println(answer2);
        String answer3 = separateChatAssistant.chat(2, "我是谁");
        System.out.println(answer3);
    }
}
