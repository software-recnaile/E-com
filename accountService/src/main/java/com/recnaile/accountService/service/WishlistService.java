package com.recnaile.accountService.service;

import com.recnaile.accountService.exception.ResourceNotFoundException;
import com.recnaile.accountService.model.ProductDTO;
import com.recnaile.accountService.model.Wishlist;
import com.recnaile.accountService.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final RestTemplate restTemplate;

    private static final String PRODUCT_SERVICE_URL = "https://product-service-4psw.onrender.com/api/products/unique/";

    public Wishlist addToWishlist(String userId, String uniqueProductName) {
        // First verify the product exists
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

        // Add new item with basic info
        Wishlist.WishlistItem item = new Wishlist.WishlistItem();
        item.setUniqueProductName(uniqueProductName);
        wishlist.getItems().add(item);

        return wishlistRepository.save(wishlist);
    }

    public Wishlist getWishlistWithProductDetails(String userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user"));

        // Enrich with product details
        wishlist.setItems(wishlist.getItems().stream()
                .map(item -> {
                    ProductDTO product = fetchProductDetails(item.getUniqueProductName());
                    if (product != null) {
                        enrichItemWithProductData(item, product);
                    }
                    return item;
                })
                .collect(Collectors.toList()));

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

    private ProductDTO fetchProductDetails(String uniqueProductName) {
        try {
            return restTemplate.getForObject(
                    PRODUCT_SERVICE_URL + uniqueProductName,
                    ProductDTO.class
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to fetch product details: " + e.getMessage());
        }
    }

    private void enrichItemWithProductData(Wishlist.WishlistItem item, ProductDTO product) {
        item.setProductId(product.get_id());
        item.setProductName(product.getProductName());
        item.setProductThumbnail(product.getProductThumbnail());
        item.setProductDescription(product.getProductDescription());
        item.setProductCategory(product.getProductCategory());
        item.setMrpRate(product.getMrpRate());
        item.setDiscountAmount(product.getDiscountAmount());
        item.setFinalPrice(product.getMrpRate() - product.getDiscountAmount());
        item.setProductImages(product.getProductImages());
    }
}