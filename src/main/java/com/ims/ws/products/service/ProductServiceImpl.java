package com.ims.ws.products.service;

import com.ims.ws.products.rest.CreateProductRestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel productRestModel) throws Exception {
        String productId = UUID.randomUUID().toString();

        // TODO: Persist Product Details into a database table before publishing and Event

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                productRestModel.title(),
                productRestModel.price(),
                productRestModel.quantity());

        LOGGER.info("***** Before publishing a ProductCreatedEvent");

        SendResult<String, ProductCreatedEvent> result = kafkaTemplate
                .send("product-create-events-topic", productId, productCreatedEvent).get();

        LOGGER.info("***** Topic: {}", result.getRecordMetadata().topic());
        LOGGER.info("***** Partition: {}", result.getRecordMetadata().partition());
        LOGGER.info("***** Offset: {}", result.getRecordMetadata().offset());
        LOGGER.info("***** Timestamp: {}", result.getRecordMetadata().timestamp());

        LOGGER.info("***** Returning from createProduct() with product Id: {}", productId);
        return productId;
    }

    /*
    public String createProduct(CreateProductRestModel productRestModel, String UnusedProductId) {
        String productId = UUID.randomUUID().toString();

        // TODO: Persist Product Details into a database table before publishing and Event

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                productRestModel.title(),
                productRestModel.price(),
                productRestModel.quantity());

        CompletableFuture<SendResult<String, ProductCreatedEvent>> future = kafkaTemplate
                .send("product-create-events-topic", productId, productCreatedEvent);

        future.whenComplete((result, exception) -> {
            if(exception != null){
                LOGGER.error("***** Failed to send message: {}", exception.getMessage() );
            } else {
                LOGGER.info("***** Message sent successfully: {}", result.getRecordMetadata().topic());
            }
        });

        // Wait for the future to complete - Makes this code synchronous!
        future.join();

        LOGGER.info("***** Returning product Id");
        return productId;
    }
    */
}
