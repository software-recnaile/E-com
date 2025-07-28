package com.recnaile.productService.controller;

import com.recnaile.productService.dto.ProductResponse;
import com.recnaile.productService.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            response.setThumbnailUrl(product.getImageUrls().get(0));
        }
        return response;
    }
}