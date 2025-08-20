package com.recnaile.wishlistService.controller;


import com.recnaile.wishlistService.model.Wishlist;
import com.recnaile.wishlistService.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/user/{userId}/product/{uniqueProductName}")
    public ResponseEntity<Wishlist> addToWishlist(
            @PathVariable String userId,
            @PathVariable String uniqueProductName) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishlistService.addToWishlist(userId, uniqueProductName));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Wishlist> getWishlist(
            @PathVariable String userId) {
        return ResponseEntity.ok(wishlistService.getWishlistWithProductDetails(userId));
    }

    @DeleteMapping("/user/{userId}/product/{uniqueProductName}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable String userId,
            @PathVariable String uniqueProductName) {
        wishlistService.removeFromWishlist(userId, uniqueProductName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearWishlist(
            @PathVariable String userId) {
        wishlistService.clearWishlist(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    public String display(){
        return "Wishlist service ";

    }
}

