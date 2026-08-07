package com.iftrue.order.presentation.controller;

import com.iftrue.order.application.service.OrderService;
import com.iftrue.order.presentation.controller.api.OrderApi;
import com.iftrue.order.presentation.dto.OrderCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @Override
    public ResponseEntity<Void> createOrder(OrderCreateRequest request) {

        orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
