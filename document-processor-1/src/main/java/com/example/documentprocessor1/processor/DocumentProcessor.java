package com.example.documentprocessor1.processor;

import com.example.documentprocessor1.model.Document;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
@Slf4j
public class DocumentProcessor {
    private static HashSet<String> processedDocs = new HashSet<>();

    @KafkaListener(topics = "${documents.topic}")
    @SendTo("${documents.topic}")
    public Document process(Document document) {

        if (StringUtils.equals(document.getStatus(), "ERROR")) {
            log.info("RECEIVED ERROR MESSAGE: " + document.toString());
            if (processedDocs.contains(document.getId())) {
                log.error("ROLLING BACK: " + document);
            }
            return null;
        } else {

            log.info("RECEIVED: " + document.toString());
            document.setStatus("DONE");
            document.setLastModified(LocalDateTime.now().toString());
            document.setContent(document.getContent() + "(processor1)");
            processedDocs.add(document.getId());
            log.info("PROCESSED: " + document.toString());
            return document;
        }
    }
}
