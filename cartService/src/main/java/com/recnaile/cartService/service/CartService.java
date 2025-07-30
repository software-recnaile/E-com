//package com.recnaile.cartService.service;
//
//
//import com.recnaile.cartService.exception.ResourceNotFoundException;
//import com.recnaile.cartService.model.Cart;
//import com.recnaile.cartService.model.ProductDTO;
//import com.recnaile.cartService.repository.CartRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class CartService {
//    private final CartRepository cartRepository;
//    private final RestTemplate restTemplate;
//
//    private static final String PRODUCT_SERVICE_URL = "https://product-service-4psw.onrender.com/api/products/unique/";
//
//    public Cart addToCart(String userId, String uniqueProductName, int quantity) {
//        // Validate quantity
//        if (quantity <= 0) {
//            throw new IllegalArgumentException("Quantity must be greater than 0");
//        }
//
//        // Verify product exists
//        ProductDTO product = fetchProductDetails(uniqueProductName);
//        if (product == null) {
//            throw new ResourceNotFoundException("Product not found with unique name: " + uniqueProductName);
//        }
//
//        // Check available stock
//        if (product.getAvailableStock() < quantity) {
//            throw new IllegalArgumentException("Not enough stock available");
//        }
//
//        // Get or create cart
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseGet(() -> {
//                    Cart newCart = new Cart();
//                    newCart.setUserId(userId);
//                    return cartRepository.save(newCart);
//                });
//
//        // Check if product already exists in cart
//        Optional<Cart.CartItem> existingItem = cart.getItems().stream()
//                .filter(item -> item.getUniqueProductName().equals(uniqueProductName))
//                .findFirst();
//
//        if (existingItem.isPresent()) {
//            // Update quantity if product already in cart
//            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
//        } else {
//            // Add new item to cart
//            Cart.CartItem newItem = new Cart.CartItem();
//            newItem.setUniqueProductName(uniqueProductName);
//            newItem.setQuantity(quantity);
//            cart.getItems().add(newItem);
//        }
//
//        return cartRepository.save(cart);
//    }
//
//    public Cart getCartWithProductDetails(String userId) {
//        // Return empty cart if not found instead of throwing error
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseGet(() -> new Cart(userId));
//
//        // Enrich with product details
//        cart.setItems(cart.getItems().stream()
//                .map(item -> {
//                    ProductDTO product = fetchProductDetails(item.getUniqueProductName());
//                    if (product != null) {
//                        enrichItemWithProductData(item, product);
//                    }
//                    return item;
//                })
//                .toList());
//
//        return cart;
//    }
//
//    public Cart updateCartItemQuantity(String userId, String uniqueProductName, int quantity) {
//        if (quantity <= 0) {
//            throw new IllegalArgumentException("Quantity must be greater than 0");
//        }
//
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
//
//        // Verify product exists and check stock
//        ProductDTO product = fetchProductDetails(uniqueProductName);
//        if (product == null) {
//            throw new ResourceNotFoundException("Product not found with unique name: " + uniqueProductName);
//        }
//        if (product.getAvailableStock() < quantity) {
//            throw new IllegalArgumentException("Not enough stock available");
//        }
//
//        // Find and update the item
//        cart.getItems().stream()
//                .filter(item -> item.getUniqueProductName().equals(uniqueProductName))
//                .findFirst()
//                .ifPresentOrElse(
//                        item -> item.setQuantity(quantity),
//                        () -> { throw new ResourceNotFoundException("Product not found in cart"); }
//                );
//
//        return cartRepository.save(cart);
//    }
//
//    public void removeFromCart(String userId, String uniqueProductName) {
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
//
//        boolean removed = cart.getItems().removeIf(
//                item -> item.getUniqueProductName().equals(uniqueProductName));
//
//        if (!removed) {
//            throw new ResourceNotFoundException("Product not found in cart");
//        }
//
//        if (cart.getItems().isEmpty()) {
//            cartRepository.delete(cart);
//        } else {
//            cartRepository.save(cart);
//        }
//    }
//
//    public void clearCart(String userId) {
//        cartRepository.deleteByUserId(userId);
//    }
//
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
//
//    private void enrichItemWithProductData(Cart.CartItem item, ProductDTO product) {
//        item.setProductId(product.get_id());
//        item.setProductName(product.getProductName());
//        item.setProductThumbnail(product.getProductThumbnail());
//        item.setMrpRate(product.getMrpRate());
//        item.setDiscountAmount(product.getDiscountAmount());
//        item.setFinalPrice(product.getMrpRate() - product.getDiscountAmount());
//        item.setAvailableStock(product.getAvailableStock());
//        item.setProductImages(product.getProductImages());
//    }
//}


package com.recnaile.cartService.service;

import com.recnaile.cartService.exception.ResourceNotFoundException;
import com.recnaile.cartService.model.Cart;
import com.recnaile.cartService.model.ProductDTO;
import com.recnaile.cartService.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;

    private static final String PRODUCT_SERVICE_URL = "https://product-service-4psw.onrender.com/api/products/unique/";

    public Cart addToCart(String userId, String uniqueProductName, int quantity) {
        // Validate input
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // Fetch product details
        ProductDTO product = fetchProductDetails(uniqueProductName);
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
            ProductDTO product = fetchProductDetails(item.getUniqueProductName());
            if (product != null) {
                enrichItemWithProductData(item, product);
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

        ProductDTO product = fetchProductDetails(uniqueProductName);
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
}