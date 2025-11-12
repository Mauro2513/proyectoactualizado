package com.maddelivery.maddelivery.servicio;

import com.maddelivery.maddelivery.io.OrderRequest;
import com.maddelivery.maddelivery.io.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    OrderResponse createOrderWithPayment(OrderRequest request);

    List<OrderResponse> getUserOrders();

    void removeOrder(String orderId);

    // ❌ DEPRECATED - No usar para admin panel
    List<OrderResponse> getOrdersOfAllUsers();

    // ✅ NUEVO - Con paginación optimizada
    Page<OrderResponse> getOrdersOfAllUsersPaginated(int page, int size);

    void updateOrderStatus(String orderId, String status);

}