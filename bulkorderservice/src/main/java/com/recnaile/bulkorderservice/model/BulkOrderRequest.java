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
    private String quantity;
    private String deliveryDate;
    private String additionalNotes;
    private String companyType;
    private String taxId;
    private String shippingAddress;
    private String billingAddress;
    private String paymentTerms;
    private boolean sameAsShipping;
    private Map<String, List<String>> selectedProducts;


}