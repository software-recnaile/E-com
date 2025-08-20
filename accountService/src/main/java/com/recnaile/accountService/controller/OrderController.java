

package com.recnaile.accountService.controller;

import com.recnaile.accountService.model.ActivityLog;
import com.recnaile.accountService.model.Order;
import com.recnaile.accountService.model.OrderRequest;
import com.recnaile.accountService.service.ActivityLogService;
import com.recnaile.accountService.service.AddressService;
import com.recnaile.accountService.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final AddressService addressService;
    private final ActivityLogService activityLogService;

    public OrderController(OrderService orderService, AddressService addressService, ActivityLogService activityLogService) {
        this.orderService = orderService;
        this.addressService = addressService;
        this.activityLogService = activityLogService;
    }

    @PostMapping("/place-order/user/{userId}")


    public ResponseEntity<Order> placeOrder(
            @RequestParam String email,

            @Valid @RequestBody OrderRequest request) {

        Order order = orderService.placeOrder(
                email,
                request.getItems(),
                request.getShippingAddressId(),
                request.getBillingAddressId(),
                request.getPaymentMethod()
        );

        // Log the activity
        activityLogService.logActivity(
                email,

                "ORDER_CREATED",
                String.format("%s created order %s at %s",
                         email, order.getOrderNumber(), LocalDateTime.now())
        );

        return ResponseEntity.ok(order);
    }


    public ResponseEntity<Order> placeOrder(
            @RequestBody OrderRequest request,
            @RequestParam String email) {

        Order order = orderService.placeOrder(

                email,
                request.getItems(),
                request.getShippingAddressId(),
                request.getBillingAddressId(),
                request.getPaymentMethod()
        );

        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
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
            @RequestParam String status,
            @RequestParam String email
            ) {

        Order order = orderService.updateOrderStatus(orderNumber, status);

        // Log the activity
        activityLogService.logActivity(
                email,

                "ORDER_STATUS_UPDATED",
                String.format("%s  updated order %s status to %s at %s",
                         email, orderNumber, status, LocalDateTime.now())
        );

        return ResponseEntity.ok(order);
    }


    @PatchMapping("/{orderNumber}/payment-status")
    public ResponseEntity<Order> updatePaymentStatus(
            @PathVariable String orderNumber,
            @RequestParam String status,
            @RequestParam String email
            ) {

        Order order = orderService.updatePaymentStatus(orderNumber, status);

        // Log the activity
        activityLogService.logActivity(
                email,

                "PAYMENT_STATUS_UPDATED",
                String.format("%s  updated order %s payment status to %s at %s",
                         email, orderNumber, status, LocalDateTime.now())
        );

        return ResponseEntity.ok(order);
    }



    @GetMapping("/")
    public String display(){
        return "Display Order";
    }
}
