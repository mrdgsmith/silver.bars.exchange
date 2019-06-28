package marketplace.repository;

import marketplace.domain.Event;
import marketplace.domain.Order;
import marketplace.domain.TinyType.OrderId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static marketplace.domain.Order.OrderBuilder.anOrder;
import static marketplace.domain.TinyType.OrderId.OrderIdBuilder.anOrderId;

public class OrderRepositoryInMemory implements OrderRepository {

    private final Map<OrderId, Order> orderMap;
    private final AtomicInteger orderIdCounter;


    public OrderRepositoryInMemory(final Map<OrderId, Order> orderMap) {
        this.orderMap = orderMap;
        this.orderIdCounter = new AtomicInteger(orderMap.size());
    }

    @Override
    public OrderId save(final Order order) {
        OrderId orderId = anOrderId()
                .withId((long) orderIdCounter.incrementAndGet())
                .build();
        orderMap.putIfAbsent(orderId, anOrder()
                .withUserId(order.getUserId())
                .withPricePerKilogram(order.getPricePerKilogram())
                .withQuantity(order.getQuantity())
                .withType(order.getType())
                .build());
        return orderId;
    }

    @Override
    public void createEvent(OrderId orderId, Event event) {

    }

    @Override
    public List<Order> getLiveOrders() {
        return null;
    }
}
