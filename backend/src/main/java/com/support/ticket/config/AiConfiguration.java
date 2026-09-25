package com.support.ticket.config;

import com.support.ticket.ai.DeterministicEmbeddingModel;
import com.support.ticket.ai.StubChatModel;
import java.io.File;
import java.io.IOException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(RagProperties.class)
public class AiConfiguration {

    @Bean
    @Profile("test")
    @Primary
    EmbeddingModel testEmbeddingModel() {
        return new DeterministicEmbeddingModel();
    }

    @Bean
    @Profile("test")
    @Primary
    ChatModel testChatModel() {
        return new StubChatModel();
    }

    @Bean
    @Profile("!test")
    @ConditionalOnMissingBean(EmbeddingModel.class)
    EmbeddingModel fallbackEmbeddingModel() {
        return new DeterministicEmbeddingModel();
    }

    @Bean
    @Profile("!test")
    @ConditionalOnMissingBean(ChatModel.class)
    ChatModel fallbackChatModel() {
        return new StubChatModel();
    }

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    VectorStore vectorStore(EmbeddingModel embeddingModel, @Value("${app.rag.vector-store-path:./data/vector-store.json}") String path)
            throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();
        if (file.exists()) {
            store.load(file);
        }
        return store;
    }

    @Bean
    @ConditionalOnProperty(name = "app.rag.persist-on-shutdown", havingValue = "true")
    VectorStorePersistence vectorStorePersistence(
            VectorStore vectorStore, @Value("${app.rag.vector-store-path:./data/vector-store.json}") String path) {
        return new VectorStorePersistence(vectorStore, path);
    }

    static class VectorStorePersistence {
        VectorStorePersistence(VectorStore vectorStore, String path) {
            if (vectorStore instanceof SimpleVectorStore simple) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        simple.save(new File(path));
                    } catch (Exception ignored) {
                        // best effort
                    }
                }));
            }
        }
    }
}
