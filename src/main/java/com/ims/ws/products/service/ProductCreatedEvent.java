package com.ims.ws.products.service;

import java.math.BigDecimal;

public record ProductCreatedEvent(String productId, String title, BigDecimal price, Integer quantity){}