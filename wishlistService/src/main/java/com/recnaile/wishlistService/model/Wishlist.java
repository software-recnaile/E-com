package com.recnaile.wishlistService.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wishlists")
public class Wishlist {
    @Id
    private String id;
    private String userId;
    private List<WishlistItem> items = new ArrayList<>();

    // Convenience constructor
    public Wishlist(String userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    @Data
    public static class WishlistItem {
        private String uniqueProductName;

        // Fields populated from product API
        private transient String productId;
        private transient String productName;
        private transient String productThumbnail;
        private transient String productDescription;
        private transient String productCategory;
        private transient double mrpRate;
        private transient double discountAmount;
        private transient double finalPrice;
        private transient List<String> productImages;
    }
}
