package com.iftrue.order.application.service;

import com.iftrue.order.domain.Order;
import com.iftrue.order.domain.OrderRepository;
import com.iftrue.order.presentation.dto.OrderCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public void createOrder(OrderCreateRequest request) {
        Order newOrder = Order.create(request.receiverCompanyId(),
                request.supplierCompanyId(),
                request.productId(),
                request.quantity(),
                request.requestMessage());

        orderRepository.save(newOrder);
    }
}
