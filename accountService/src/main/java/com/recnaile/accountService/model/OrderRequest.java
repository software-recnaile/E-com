package com.recnaile.accountService.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotEmpty(message = "Items cannot be empty")
    private List<Item> items;

    @NotNull(message = "Shipping address ID is required")
    private String shippingAddressId;

    @NotNull(message = "Billing address ID is required")
    private String billingAddressId;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    @Data
    public static class Item {
        @NotNull(message = "Product unique name is required")
        private String uniqueProductName;

        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}