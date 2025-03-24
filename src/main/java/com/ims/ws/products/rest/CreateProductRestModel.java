package com.ims.ws.products.rest;

import java.math.BigDecimal;

public record CreateProductRestModel (String title, BigDecimal price, Integer quantity
){}
