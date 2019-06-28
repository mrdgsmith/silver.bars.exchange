package marketplace.repository;

import marketplace.domain.Event;
import marketplace.domain.Order;
import marketplace.domain.TinyType.OrderId;

import java.util.List;

public interface OrderRepository {
    OrderId save(final Order order);

    void createEvent(final OrderId orderId, final Event event);

    List<Order> getLiveOrders();
}