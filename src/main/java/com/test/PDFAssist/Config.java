package com.test.PDFAssist;


import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.cassandra.AstraDbEmbeddingConfiguration;
import dev.langchain4j.store.embedding.cassandra.AstraDbEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.chain.ConversationalRetrievalChain;

import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Configuration
public class Config {

    @Bean
    public AllMiniLmL6V2EmbeddingModel embeddingModel(){
        return new AllMiniLmL6V2EmbeddingModel();
    }
    @Bean
    public AstraDbEmbeddingStore astraDbEmbeddingStore(){

        //Add your astra db credentials...
        String astratoken="";
        String databaseId="";
        return new AstraDbEmbeddingStore(
                AstraDbEmbeddingConfiguration.builder().token(astratoken).databaseId(databaseId).databaseRegion("us-east1").keyspace("demo").table("demo2").dimension(384).build());

    }
    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 0))
                .embeddingModel(embeddingModel())
                .embeddingStore(astraDbEmbeddingStore())
                .build();
    }
    @Bean
    public ConversationalRetrievalChain conversationalRetrievalChain() {
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(OpenAiChatModel.withApiKey("your-open-api-key"))
                .retriever(EmbeddingStoreRetriever.from(astraDbEmbeddingStore(), embeddingModel()))
                .build();
    }

}
