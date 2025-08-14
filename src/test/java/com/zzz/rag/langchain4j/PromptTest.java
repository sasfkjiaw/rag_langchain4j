package com.zzz.rag.langchain4j;

import com.zzz.rag.langchain4j.assistant.MemoryChatAssistant;
import com.zzz.rag.langchain4j.assistant.SeparateChatAssistant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PromptTest {

    @Autowired
    private SeparateChatAssistant separateChatAssistant;

    @Test
    public void testSystemMessage() {
        String answer = separateChatAssistant.chat(5,"你是谁，今天几号？");
        System.out.println(answer);
    }

    @Autowired
    private MemoryChatAssistant memoryChatAssistant;

    @Test
    public void testUserMessage() {
        String answer = memoryChatAssistant.chat("我是yudy");
        System.out.println(answer);
    }


    @Test
    public void testV() {
        String answer1 = separateChatAssistant.chat2(1, "我是yudy");
        System.out.println(answer1);
        String answer2 = separateChatAssistant.chat2(1, "我是谁");
        System.out.println(answer2);
    }

    @Test
    public void testUserInfo() {
        String answer = separateChatAssistant.chat3(1, "我是谁，我多大了", "翠花", 18);
        System.out.println(answer);
    }
}