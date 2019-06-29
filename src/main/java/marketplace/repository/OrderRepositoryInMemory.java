package marketplace.repository;

import marketplace.domain.Event;
import marketplace.domain.EventType;
import marketplace.domain.Order;
import marketplace.domain.TinyType.OrderId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toUnmodifiableList;
import static marketplace.domain.Event.EventBuilder.anEvent;
import static marketplace.domain.EventType.CANCEL;
import static marketplace.domain.EventType.LIVE;
import static marketplace.domain.Order.OrderBuilder.anOrder;
import static marketplace.domain.TinyType.OrderId.OrderIdBuilder.anOrderId;

public class OrderRepositoryInMemory implements OrderRepository {

    private final Map<OrderId, marketplace.repository.entity.Order> orderMap;
    private final AtomicInteger orderIdCounter;

    public OrderRepositoryInMemory(final Map<OrderId, marketplace.repository.entity.Order> orderMap) {
        this.orderMap = orderMap;
        this.orderIdCounter = new AtomicInteger(orderMap.size());
    }

    @Override
    public OrderId save(final Order order) {
        OrderId orderId = anOrderId()
                .withId((long) orderIdCounter.incrementAndGet())
                .build();
        orderMap.putIfAbsent(orderId, marketplace.repository.entity.Order.OrderBuilder.anOrder()
                .withUserId(order.getUserId())
                .withPricePerKilogram(order.getPricePerKilogram())
                .withQuantity(order.getQuantity())
                .withType(order.getType())
                .withEvents(List.of(anEvent()
                        .withEventType(LIVE)
                        .withTimeStamp(now())
                        .build()))
                .build());
        return orderId;
    }

    @Override
    public void createEvent(final OrderId orderId, final EventType eventType) {
        orderMap.computeIfPresent(orderId, (id, order) -> {
            order.addEvent(anEvent()
                    .withEventType(eventType)
                    .withTimeStamp(now())
                    .build());
            return order;
        });

    }

    @Override
    public List<Order> getLiveOrders() {
        return orderMap.values().stream()
                .filter(order -> !order.getEvents().stream()
                        .map(Event::getEventType)
                        .collect(toUnmodifiableList()).contains(CANCEL))
                .map(order -> anOrder()
                        .withType(order.getType())
                        .withUserId(order.getUserId())
                        .withQuantity(order.getQuantity())
                        .withPricePerKilogram(order.getPricePerKilogram())
                        .build())
                .collect(toUnmodifiableList());
    }
}
