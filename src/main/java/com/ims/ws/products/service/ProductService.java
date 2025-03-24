package com.ims.ws.products.service;

import com.ims.ws.products.rest.CreateProductRestModel;

public interface ProductService {
    String createProduct(CreateProductRestModel productRestModel) throws Exception;
}
