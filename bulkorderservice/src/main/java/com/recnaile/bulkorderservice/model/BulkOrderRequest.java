package com.recnaile.bulkorderservice.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BulkOrderRequest {
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String companyType;
    private String taxId;
    private Map<String, List<String>> selectedProducts;
    private Integer quantity;
    private String deliveryDate;
    private String shippingAddress;
    private String billingAddress;
    private String paymentTerms;
    private String additionalNotes;
    private boolean sameAsShipping;
}