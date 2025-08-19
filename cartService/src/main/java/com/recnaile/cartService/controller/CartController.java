package com.recnaile.cartService.controller;


import com.recnaile.cartService.model.Cart;
import com.recnaile.cartService.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/user/{userId}/product/{uniqueProductName}")
    public ResponseEntity<Cart> addToCart(
            @PathVariable String userId,
            @PathVariable String uniqueProductName,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addToCart(userId, uniqueProductName, quantity));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Cart> getCart(
            @PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCartWithProductDetails(userId));
    }

    @PutMapping("/user/{userId}/product/{uniqueProductName}")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @PathVariable String userId,
            @PathVariable String uniqueProductName,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, uniqueProductName, quantity));
    }

    @DeleteMapping("/user/{userId}/product/{uniqueProductName}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable String userId,
            @PathVariable String uniqueProductName) {
        cartService.removeFromCart(userId, uniqueProductName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearCart(
            @PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @Getting(/)
    public String display(){
    return "Cart Service";
    }
}


