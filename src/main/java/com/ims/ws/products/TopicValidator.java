package com.ims.ws.products;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Component
public class TopicValidator {

    private static final Logger log = LoggerFactory.getLogger(TopicValidator.class);

    private final AdminClient adminClient;

    public TopicValidator(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkTopicConfiguration() throws ExecutionException, InterruptedException {
        try {
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(
                    Collections.singletonList("product-create-events-topic")
            );

            TopicDescription topicDescription = describeTopicsResult.all().get().get("product-create-events-topic");

            log.info("Topic configuration: {}", topicDescription);

            // Verify partitions and replicas
            topicDescription.partitions().forEach(partitionInfo -> {
                if (partitionInfo.replicas().size() != 3) {
                    throw new IllegalStateException("Partition " + partitionInfo.partition() + " has incorrect replica count");
                }
            });

            log.info("Topic 'product-create-events-topic' is properly configured.");

        } catch (Exception e) {
            log.error("Error validating topic configuration: ", e);
            throw e; // Re-throw to prevent the application from starting if topic is not valid
        }
    }
}
