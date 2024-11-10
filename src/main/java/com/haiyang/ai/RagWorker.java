package com.haiyang.ai;


import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class RagWorker {

    private final VectorStore vectorStore;
    private final Resource pdfResource;

    public RagWorker(VectorStore vectorStore,
                     @Value("classpath:/rag/1.pdf") Resource pdfResource) {
        this.vectorStore = vectorStore;
        this.pdfResource = pdfResource;
    }

    @PostConstruct
    public void init() {
        //Extract
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(pdfResource);
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        //Transform, and Load
        vectorStore.add(tokenTextSplitter.apply(tikaDocumentReader.get()));
    }
}