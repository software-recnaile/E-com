package com.recnaile.cartService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recnaile.cartService.exception.ResourceNotFoundException;
import com.recnaile.cartService.model.Cart;
import com.recnaile.cartService.model.ProductDTO;
import com.recnaile.cartService.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String PRODUCT_SERVICE_URL = "https://api.recnaile.com/api/products/unique/";

    public Cart addToCart(String userId, String uniqueProductName, int quantity) {
        // Validate input
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // Fetch product details using safe method
        ProductDTO product = fetchProductDetailsSafe(uniqueProductName);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found: " + uniqueProductName);
        }

        // Check stock availability
        if (product.getAvailableStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        // Get or create user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        // Handle existing or new cart item
        cart.getItems().stream()
                .filter(item -> item.getUniqueProductName().equals(uniqueProductName))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> addNewCartItem(cart, product, quantity)
                );

        return cartRepository.save(cart);
    }

    /**
     * COMPLETELY NEW METHOD - Replaces the old fetchProductDetails
     * This manually handles the response to avoid HttpMessageConverter issues
     */
    private ProductDTO fetchProductDetailsSafe(String uniqueProductName) {
        String url = null;
        try {
            // Validate and encode the product name
            if (uniqueProductName == null || uniqueProductName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be null or empty");
            }
            
            String encodedProductName = URLEncoder.encode(uniqueProductName.trim(), StandardCharsets.UTF_8);
            url = PRODUCT_SERVICE_URL + encodedProductName;
            
            log.info("🔍 Fetching product from: {}", url);
            
            // Create request with explicit JSON acceptance
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("User-Agent", "CartService/1.0");
            
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            
            // MANUAL RESPONSE HANDLING - get raw response as String
            ResponseEntity<String> response = restTemplate.exchange(
                URI.create(url),
                HttpMethod.GET,
                requestEntity,
                String.class
            );
            
            log.info("📥 Response Status: {}", response.getStatusCode());
            log.info("📥 Content Type: {}", response.getHeaders().getContentType());
            
            // Check if we got HTML (error page)
            if (response.getHeaders().getContentType() != null && 
                response.getHeaders().getContentType().includes(MediaType.TEXT_HTML)) {
                
                log.error("❌ Received HTML error page instead of JSON");
                log.error("📄 HTML Preview: {}", 
                    response.getBody() != null ? 
                    response.getBody().substring(0, Math.min(300, response.getBody().length())) : "Empty body");
                
                throw new ResourceNotFoundException("Product service returned HTML error page. Product '" + uniqueProductName + "' may not exist.");
            }
            
            // Check if response is successful and has body
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResourceNotFoundException("Product service returned error: " + response.getStatusCode());
            }
            
            if (response.getBody() == null || response.getBody().trim().isEmpty()) {
                throw new ResourceNotFoundException("Empty response from product service");
            }
            
            // Log the raw response for debugging
            log.debug("📄 Raw response body: {}", response.getBody());
            
            // Manually parse JSON to avoid HttpMessageConverter issues
            log.info("🔄 Parsing JSON response...");
            ProductDTO product = objectMapper.readValue(response.getBody(), ProductDTO.class);
            
            if (product == null) {
                throw new ResourceNotFoundException("Failed to parse product data");
            }
            
            log.info("✅ Successfully fetched product: {} (ID: {})", 
                product.getProductName(), product.get_id());
            
            return product;
            
        } catch (HttpClientErrorException.NotFound e) {
            log.error("❌ Product not found (404): {}", uniqueProductName);
            throw new ResourceNotFoundException("Product not found: " + uniqueProductName);
        } catch (HttpClientErrorException e) {
            log.error("❌ HTTP Client Error {} for product '{}': {}", 
                e.getStatusCode(), uniqueProductName, e.getMessage());
            throw new ResourceNotFoundException("Product service error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("❌ Unexpected error fetching product '{}' from URL {}: {}", 
                uniqueProductName, url, e.getMessage(), e);
            throw new ResourceNotFoundException("Error fetching product '" + uniqueProductName + "': " + e.getMessage());
        }
    }

    private void addNewCartItem(Cart cart, ProductDTO product, int quantity) {
        Cart.CartItem newItem = new Cart.CartItem();
        newItem.setUniqueProductName(product.getUniqueProductName());
        newItem.setQuantity(quantity);
        newItem.setProductId(product.get_id());
        newItem.setProductName(product.getProductName());
        newItem.setMrpRate(product.getMrpRate());
        newItem.setDiscountAmount(product.getDiscountAmount());
        newItem.setFinalPrice(product.getMrpRate() - product.getDiscountAmount());
        newItem.setAvailableStock(product.getAvailableStock());
        newItem.setProductImages(product.getImageUrls());

        // Set thumbnail - prioritize productThumbnail, fallback to first imageUrl
        String thumbnail = product.getProductThumbnail();
        if ((thumbnail == null || thumbnail.isEmpty())) {
            List<String> imageUrls = product.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                thumbnail = imageUrls.get(0);
            }
        }
        newItem.setProductThumbnail(thumbnail);

        cart.getItems().add(newItem);
    }

    public Cart getCartWithProductDetails(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> new Cart(userId));

        cart.getItems().forEach(item -> {
            try {
                ProductDTO product = fetchProductDetailsSafe(item.getUniqueProductName());
                if (product != null) {
                    enrichItemWithProductData(item, product);
                }
            } catch (ResourceNotFoundException e) {
                log.warn("Could not refresh product data for {}: {}", 
                    item.getUniqueProductName(), e.getMessage());
                // Keep existing item data if product can't be fetched
            }
        });

        return cart;
    }

    public Cart updateCartItemQuantity(String userId, String uniqueProductName, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        ProductDTO product = fetchProductDetailsSafe(uniqueProductName);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        if (product.getAvailableStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        cart.getItems().stream()
                .filter(item -> item.getUniqueProductName().equals(uniqueProductName))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(quantity),
                        () -> { throw new ResourceNotFoundException("Item not in cart"); }
                );

        return cartRepository.save(cart);
    }

    public void removeFromCart(String userId, String uniqueProductName) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        boolean removed = cart.getItems().removeIf(
                item -> item.getUniqueProductName().equals(uniqueProductName));

        if (!removed) {
            throw new ResourceNotFoundException("Item not in cart");
        }

        if (cart.getItems().isEmpty()) {
            cartRepository.delete(cart);
        } else {
            cartRepository.save(cart);
        }
    }

    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }

    private void enrichItemWithProductData(Cart.CartItem item, ProductDTO product) {
        // Only update fields that might change from the product service
        item.setProductName(product.getProductName());
        item.setMrpRate(product.getMrpRate());
        item.setDiscountAmount(product.getDiscountAmount());
        item.setFinalPrice(product.getMrpRate() - product.getDiscountAmount());
        item.setAvailableStock(product.getAvailableStock());

        // Update thumbnail if missing or if product has new thumbnail
        if (item.getProductThumbnail() == null ||
                (product.getProductThumbnail() != null && !product.getProductThumbnail().isEmpty())) {
            String thumbnail = product.getProductThumbnail();
            if ((thumbnail == null || thumbnail.isEmpty()) &&
                    product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                thumbnail = product.getImageUrls().get(0);
            }
            item.setProductThumbnail(thumbnail);
        }

        // Update images if null or empty
        if (item.getProductImages() == null || item.getProductImages().isEmpty()) {
            item.setProductImages(product.getImageUrls());
        }
    }

    /**
     * DELETE THIS OLD METHOD IF IT EXISTS IN YOUR CODE
     * This is the method causing the HttpMessageConverter error
     */
    /*
    private ProductDTO fetchProductDetails(String uniqueProductName) {
        try {
            return restTemplate.getForObject(
                    PRODUCT_SERVICE_URL + uniqueProductName,
                    ProductDTO.class
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error fetching product: " + e.getMessage());
        }
    }
    */
}
