//package com.recnaile.accountService.controller;
//
//import com.recnaile.accountService.model.Order;
//import com.recnaile.accountService.service.OrderService;
//import jakarta.validation.constraints.NotBlank;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/orders")
//public class OrderController {
//    private final OrderService orderService;
//
//    public OrderController(OrderService orderService) {
//        this.orderService = orderService;
//    }
//
//    @PostMapping("/place-order/user/{userId}")
//    public ResponseEntity<Order> placeOrder(
//            @PathVariable String userId,
//            @RequestParam @NotBlank String shippingAddressId,
//            @RequestParam @NotBlank String billingAddressId,
//            @RequestParam @NotBlank String paymentMethod) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(orderService.placeOrder(userId, shippingAddressId, billingAddressId, paymentMethod));
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userId) {
//        return ResponseEntity.ok(orderService.getUserOrders(userId));
//    }
//
//    @GetMapping("/{orderNumber}/user/{userId}")
//    public ResponseEntity<Order> getOrder(
//            @PathVariable String orderNumber,
//            @PathVariable String userId) {
//        return ResponseEntity.ok(orderService.getOrder(orderNumber, userId));
//    }
//}

package com.recnaile.accountService.controller;

import com.recnaile.accountService.model.Order;
import com.recnaile.accountService.model.OrderRequest;
import com.recnaile.accountService.service.AddressService;
import com.recnaile.accountService.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final AddressService addressService;

    public OrderController(OrderService orderService, AddressService addressService) {
        this.orderService = orderService;
        this.addressService = addressService;
    }

    @PostMapping("/place-order/user/{userId}")
    public ResponseEntity<Order> placeOrder(
            @PathVariable String userId,
            @Valid @RequestBody OrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(
                        userId,
                        request.getItems(),
                        request.getShippingAddressId(),
                        request.getBillingAddressId(),
                        request.getPaymentMethod()
                ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderNumber}/user/{userId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable String orderNumber,
            @PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber, userId));
    }

    @PatchMapping("/{orderNumber}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String orderNumber,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderNumber, status));
    }

    @PatchMapping("/{orderNumber}/payment-status")
    public ResponseEntity<Order> updatePaymentStatus(
            @PathVariable String orderNumber,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updatePaymentStatus(orderNumber, status));
    }
    @GetMapping("/")
    public String display(){
        return "Display Order";
    }
}