package com.dg.schoolhelp.ai.config;

import lombok.AllArgsConstructor;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreAutoConfiguration;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
@EnableAutoConfiguration(exclude = {RedisVectorStoreAutoConfiguration.class})
@EnableConfigurationProperties({RedisVectorStoreProperties.class})
@AllArgsConstructor
public class RedisVectorConfig {


    /**
     * 创建RedisStack向量数据库
     *
     * @param embeddingModel 嵌入模型
     * @param jedisPooled     redis-stack的配置信息
     * @return vectorStore 向量数据库
     */
    @Bean
    public VectorStore vectorStore(JedisPooled jedisPooled, @Qualifier("ollamaEmbeddingModel")EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled,embeddingModel)
                .indexName("default-index")
                .prefix("default:")
                .initializeSchema(true)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();
    }

    private StoreConfig storeConfig;

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled(storeConfig.getHost(), storeConfig.getPort(), storeConfig.getName(),storeConfig.getPassword());
    }
}


