package com.recnaile.cartService.model;


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
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    private String userId;
    private List<CartItem> items = new ArrayList<>();

    // Convenience constructor
    public Cart(String userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    @Data
    public static class CartItem {
        private String uniqueProductName;
        private int quantity;

        // Fields populated from product API
        private  String productId;
        private transient String productName;
        private transient String productThumbnail;
        private transient double mrpRate;
        private transient double discountAmount;
        private transient double finalPrice;
        private transient int availableStock;
        private transient List<String> productImages;
    }

}
