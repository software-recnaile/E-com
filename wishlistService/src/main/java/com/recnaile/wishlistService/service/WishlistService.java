package com.recnaile.wishlistService.service;


import com.recnaile.wishlistService.exception.ResourceNotFoundException;
import com.recnaile.wishlistService.model.ProductDTO;
import com.recnaile.wishlistService.model.Wishlist;
import com.recnaile.wishlistService.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final RestTemplate restTemplate;

    private static final String PRODUCT_SERVICE_URL = "http://api.recnaile.com/api/products/unique/";

    public Wishlist addToWishlist(String userId, String uniqueProductName) {
        // First verify the product exists and get its details
        ProductDTO product = fetchProductDetails(uniqueProductName);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with unique name: " + uniqueProductName);
        }

        // Check if product already exists in wishlist
        if (wishlistRepository.existsByUserIdAndItemsUniqueProductName(userId, uniqueProductName)) {
            throw new IllegalArgumentException("Product already in wishlist");
        }

        // Get or create wishlist
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUserId(userId);
                    return newWishlist;
                });

        // Create new wishlist item with product details
        Wishlist.WishlistItem item = new Wishlist.WishlistItem();
        item.setUniqueProductName(uniqueProductName);
        item.setProductId(product.get_id());
        item.setProductName(product.getProductName());

        // Set thumbnail URL - use thumbnailUrl if exists, otherwise use first image from imageUrls
        if (product.getThumbnailUrl() != null && !product.getThumbnailUrl().isEmpty()) {
            item.setProductThumbnail(product.getThumbnailUrl());
        } else if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            item.setProductThumbnail(product.getImageUrls().get(0));
        }

        item.setProductDescription(product.getProductDescription());
        item.setProductCategory(product.getProductCategory());
        item.setMrpRate(product.getMrpRate());
        item.setDiscountAmount(product.getDiscountAmount());
        item.setFinalPrice(product.getMrpRate() - product.getDiscountAmount());
        item.setProductImages(product.getImageUrls());

        wishlist.getItems().add(item);
        return wishlistRepository.save(wishlist);
    }
    public Wishlist getWishlistWithProductDetails(String userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user"));

        // Filter out items where product details can't be fetched
        List<Wishlist.WishlistItem> validItems = wishlist.getItems().stream()
                .map(item -> {
                    try {
                        ProductDTO product = fetchProductDetails(item.getUniqueProductName());
                        if (product != null) {
                            enrichItemWithProductData(item, product);
                            return item;
                        }
                    } catch (ResourceNotFoundException e) {
                        // Log the missing product but continue processing others
                        log.warn("Product not found: {}", item.getUniqueProductName());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        wishlist.setItems(validItems);
        return wishlist;
    }


    public void removeFromWishlist(String userId, String uniqueProductName) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user"));

        boolean removed = wishlist.getItems().removeIf(
                item -> item.getUniqueProductName().equals(uniqueProductName));

        if (!removed) {
            throw new ResourceNotFoundException("Product not found in wishlist");
        }

        if (wishlist.getItems().isEmpty()) {
            wishlistRepository.delete(wishlist);
        } else {
            wishlistRepository.save(wishlist);
        }
    }

    public void clearWishlist(String userId) {
        wishlistRepository.deleteByUserId(userId);
    }

//    private ProductDTO fetchProductDetails(String uniqueProductName) {
//        try {
//            return restTemplate.getForObject(
//                    PRODUCT_SERVICE_URL + uniqueProductName,
//                    ProductDTO.class
//            );
//        } catch (Exception e) {
//            throw new ResourceNotFoundException("Failed to fetch product details: " + e.getMessage());
//        }
//    }


    private ProductDTO fetchProductDetails(String uniqueProductName) {
        try {
            String productUrl = PRODUCT_SERVICE_URL + uniqueProductName;
            System.out.println("Fetching product details from: {}"+ productUrl);

            ProductDTO product = restTemplate.getForObject(productUrl, ProductDTO.class);

            if (product != null) {
                System.out.println("Fetched product details: {}"+ product);
                // Or for more detailed logging:
                log.debug("Product details - ID: {}, Name: {}, Thumbnail: {}, Images: {}, MRP: {}, Discount: {}",
                        product.get_id(),
                        product.getProductName(),
                        product.getThumbnailUrl(),
                        product.getImageUrls(),
                        product.getMrpRate(),
                        product.getDiscountAmount());
            } else {
                log.warn("Product not found with unique name: {}", uniqueProductName);
            }

            return product;
        } catch (Exception e) {
            log.error("Failed to fetch product details for {}: {}", uniqueProductName, e.getMessage());
            throw new ResourceNotFoundException("Failed to fetch product details: " + e.getMessage());
        }
    }

    private void enrichItemWithProductData(Wishlist.WishlistItem item, ProductDTO product) {
        item.setProductId(product.get_id());
        item.setProductName(product.getProductName());

        // Set thumbnail - use productThumbnail if exists, otherwise use first image from productImages
        if (product.getThumbnailUrl() != null && !product.getThumbnailUrl().isEmpty()) {
            item.setProductThumbnail(product.getThumbnailUrl());
        } else if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            item.setProductThumbnail(product.getImageUrls().getFirst());
        } else {
            item.setProductThumbnail(null); // or some default image if you prefer
        }

        item.setProductDescription(product.getProductDescription());
        item.setProductCategory(product.getProductCategory());
        item.setMrpRate(product.getMrpRate());
        item.setDiscountAmount(product.getDiscountAmount());
        item.setFinalPrice(product.getMrpRate() - product.getDiscountAmount());
        item.setProductImages(product.getImageUrls());
    }



}

