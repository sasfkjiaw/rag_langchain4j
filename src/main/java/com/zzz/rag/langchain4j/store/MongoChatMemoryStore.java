package com.zzz.rag.langchain4j.store;

import com.zzz.rag.langchain4j.bean.ChatMessages;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * MongoDB 聊天记录存储实现类。
 *
 * <p>
 * 该类实现了 LangChain4j 的 {@link ChatMemoryStore} 接口，负责将聊天记录持久化到 MongoDB。
 * </p>
 *
 * <p>
 * <b>核心存储策略：</b> 采用“整体打包”的方式。对于每一个独立的对话（由 memoryId 标识），
 * 其所有的聊天记录 ({@code List<ChatMessage>}) 会被序列化成一个单一的JSON字符串，
 * 然后存储在数据库中一个文档的特定字段里。这种方式读写效率高，且能完美保证消息顺序。
 * </p>
 */
@Component
public class MongoChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 根据指定的 memoryId 从 MongoDB 中获取聊天记录。
     *
     * @param memoryId 对话的唯一标识符。
     * @return 返回一个 {@code List<ChatMessage>} 对象列表。如果找不到记录，则返回一个空列表。
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 1. 构建查询条件，根据 memoryId 查找对应的文档。
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);

        // 2. 在 chat_messages 集合中执行查询，期望返回一个 ChatMessages 类型的文档。
        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);

        // 3. 如果找不到文档，说明这是一次新的对话，直接返回一个空的LinkedList。
        if(chatMessages == null) {
            return new LinkedList<>();
        }

        // 4. 如果找到了文档，从文档的 content 字段中获取存储聊天记录的JSON字符串，
        //    并使用 Deserializer (反序列化器) 将这个JSON字符串恢复成 List<ChatMessage> Java对象列表。
        return ChatMessageDeserializer.messagesFromJson(chatMessages.getContent());
    }

    /**
     * 更新（或新增）指定 memoryId 的聊天记录。
     *
     * @param memoryId 对话的唯一标识符。
     * @param messages 最新的完整聊天记录列表。
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // 1. 使用 Serializer (序列化器) 将 {@code List<ChatMessage>} Java对象列表转换为一个单一的JSON字符串。
        //    这是将内存中的复杂对象转换为可以存入数据库的简单数据格式的关键步骤。
        String messagesAsJson = ChatMessageSerializer.messagesToJson(messages);

        // 2. 构建MongoDB的查询条件，目标是找到与当前 memoryId 匹配的文档。
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);

        // 3. 构建MongoDB的更新操作，准备将 "content" 字段的值设置为新生成的JSON字符串。
        Update update = new Update();
        update.set("content", messagesAsJson);

        // 4. 执行 `upsert` (update + insert) 操作：
        //    - 如果根据 query 能查询出文档，则修改该文档的 "content" 字段。
        //    - 如果查询不到文档，则将 query 的条件和 update 的内容合并，新增一个文档。
        mongoTemplate.upsert(query, update, ChatMessages.class);
    }

    /**
     * 根据指定的 memoryId 删除其全部聊天记录。
     *
     * @param memoryId 对话的唯一标识符。
     */
    @Override
    public void deleteMessages(Object memoryId) {
        // 1. 构建MongoDB的查询条件，目标是找到与当前 memoryId 匹配的文档。
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);

        // 2. 执行 `remove` 操作，从数据库中删除所有匹配查询条件的文档。
        mongoTemplate.remove(query, ChatMessages.class);
    }
}
