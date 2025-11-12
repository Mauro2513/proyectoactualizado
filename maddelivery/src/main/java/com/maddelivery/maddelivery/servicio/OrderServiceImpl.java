package com.maddelivery.maddelivery.servicio;

import com.maddelivery.maddelivery.entidad.OrderEntity;
import com.maddelivery.maddelivery.io.OrderRequest;
import com.maddelivery.maddelivery.io.OrderResponse;
import com.maddelivery.maddelivery.repositorio.CartRepository;
import com.maddelivery.maddelivery.repositorio.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartRepository cartRepository;


    @Override
    public OrderResponse createOrderWithPayment(OrderRequest request) {
        // ✅ CRÍTICO: Obtener el userId del usuario logueado
        String userId = userService.findByUserId();

        OrderEntity newOrder = convertToEntity(request);

        // ✅ CRÍTICO: Asignar el userId a la orden ANTES de guardar
        newOrder.setUserId(userId);

        newOrder = orderRepository.save(newOrder);
        return convertToResponse(newOrder);
    }

    @Override
    public List<OrderResponse> getUserOrders() {
        String loggedInUserId = userService.findByUserId();
        List<OrderEntity> list = orderRepository.findByUserId(loggedInUserId);
        return list.stream().map(entity -> convertToResponse(entity)).collect(Collectors.toList());
    }

    @Override
    public void removeOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    @Deprecated
    public List<OrderResponse> getOrdersOfAllUsers() {
        // ⚠️ No usar este método para admin panel con muchos datos
        List <OrderEntity> list = orderRepository.findAll();
        return list.stream().map(entity -> convertToResponse(entity)).collect(Collectors.toList());
    }

    @Override
    public Page<OrderResponse> getOrdersOfAllUsersPaginated(int page, int size) {
        // ✅ Crear Pageable con ordenamiento descendente por fecha de creación
        // Asume que tienes un campo 'createdAt' o 'fechaCreacion' en OrderEntity
        // Si no lo tienes, usa 'id' para ordenar por más reciente
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending() // Ordena por ID descendente (más recientes primero)
                // Si tienes campo de fecha: Sort.by("createdAt").descending()
        );

        // ✅ Obtener página de entidades
        Page<OrderEntity> entityPage = orderRepository.findAll(pageable);

        // ✅ Convertir a Response manteniendo la metadata de paginación
        return entityPage.map(entity -> convertToResponse(entity));
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        entity.setEstado(status);
        orderRepository.save(entity);
    }

    private OrderResponse convertToResponse(OrderEntity newOrder){
        return OrderResponse.builder()
                .id(newOrder.getId())
                .cuenta(newOrder.getCuenta())
                .direccion(newOrder.getDireccion())
                .userId(newOrder.getUserId())
                .estado(newOrder.getEstado())
                .estadoDePago(newOrder.getEstadoDePago())
                .email(newOrder.getEmail())
                .telefono(newOrder.getTelefono())
                .orderedItems(newOrder.getOrderItems())
                .build();
    }

    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .direccion(request.getDireccion())
                .cuenta(request.getCuenta())
                .orderItems(request.getOrderedItems())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .estado(request.getEstado())
                .build();
    }
}