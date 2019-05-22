package marketplace.service;

import marketplace.domain.Order;
import marketplace.repository.OrderRepository;

public class OrderManagementService {
    private OrderRepository orderRepository;

    public OrderManagementService(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void registerOrder(final Order order) {
        orderRepository.save(order);
    }
}