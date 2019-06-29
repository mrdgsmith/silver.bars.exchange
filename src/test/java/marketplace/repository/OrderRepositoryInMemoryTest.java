package marketplace.repository;

import marketplace.domain.Event;
import marketplace.domain.TinyType.OrderId;
import marketplace.repository.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toUnmodifiableList;
import static marketplace.domain.Event.EventBuilder.anEvent;
import static marketplace.domain.EventType.CANCEL;
import static marketplace.domain.EventType.LIVE;
import static marketplace.domain.Order.OrderBuilder.anOrder;
import static marketplace.domain.OrderType.BUY;
import static marketplace.domain.OrderType.SELL;
import static marketplace.domain.TinyType.Money.MoneyBuilder.aMoney;
import static marketplace.domain.TinyType.OrderId.OrderIdBuilder.anOrderId;
import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryInMemoryTest {

    private OrderRepository orderRepository;
    private Map<OrderId, Order> orderMap;

    @BeforeEach
    void setUp() {
        orderMap = new ConcurrentHashMap<>();
        orderRepository = new OrderRepositoryInMemory(orderMap);
    }

    @Test
    @DisplayName("Add order to database")
    void addOrderToDatabase() {
        final var pricePerKilogram = aMoney()
                .withPrice(4564L)
                .build();

        final var orderId = orderRepository.save(anOrder()
                .withType(SELL)
                .withQuantity(574D)
                .withPricePerKilogram(pricePerKilogram)
                .withUserId("user1")
                .build());

        final var orderIdkey = anOrderId()
                .withId(1L)
                .build();
        final var order = orderMap.get(orderIdkey);

        assertThat(orderId).isEqualTo(orderIdkey);
        assertThat(order.getPricePerKilogram()).isEqualByComparingTo(pricePerKilogram);
        assertThat(order.getType()).isEqualTo(SELL);
        assertThat(order.getQuantity()).isEqualTo(574D);
        assertThat(order.getUserId()).isEqualTo("user1");
        assertThat(order.getEvents().stream()
                .map(Event::getEventType)
                .collect(toUnmodifiableList())).containsExactly(LIVE);
    }

    @Test
    @DisplayName("Add order to database with existing orders")
    void addOrderToDatabaseWithExistingOrders() {
        final var orderId = anOrderId()
                .withId(1L)
                .build();

        orderMap.put(orderId, marketplace.repository.entity.Order.OrderBuilder.anOrder()
                .withType(SELL)
                .withEvents(new ArrayList<>())
                .build());

        final var orderRepository = new OrderRepositoryInMemory(orderMap);

        final var pricePerKilogram = aMoney()
                .withPrice(4564L)
                .build();

        final var actualOrderId = orderRepository.save(anOrder()
                .withType(SELL)
                .withQuantity(574D)
                .withPricePerKilogram(pricePerKilogram)
                .withUserId("user1")
                .build());

        final var orderIdKey = anOrderId()
                .withId(2L)
                .build();

        final var order = orderMap.get(orderIdKey);
        assertThat(actualOrderId).isEqualTo(orderIdKey);

        assertThat(order.getPricePerKilogram()).isEqualByComparingTo(pricePerKilogram);
        assertThat(order.getType()).isEqualTo(SELL);
        assertThat(order.getQuantity()).isEqualTo(574D);
        assertThat(order.getUserId()).isEqualTo("user1");
        assertThat(order.getEvents().stream()
                .map(Event::getEventType)
                .collect(toUnmodifiableList())).containsExactly(LIVE);

    }

    @Test
    @DisplayName("Given order in database when creating event then event exists")
    void GivenOderInRepositoryWhenCreatingEventThenEventExists() {
        final var orderId = anOrderId()
                .withId(1L)
                .build();

        orderMap.put(orderId, marketplace.repository.entity.Order.OrderBuilder.anOrder()
                .withType(SELL)
                .withEvents(new ArrayList<>() {
                    {
                        add(anEvent()
                                .withEventType(LIVE)
                                .build());
                    }
                })
                .build());

        final var orderRepository = new OrderRepositoryInMemory(orderMap);
        orderRepository.createEvent(orderId, CANCEL);

        final var order = orderMap.get(orderId);
        assertThat(order.getEvents().stream()
                .map(Event::getEventType)
                .collect(toUnmodifiableList())).containsExactly(LIVE, CANCEL);
    }

    @Test
    @DisplayName("Get live orders only")
    void getLiveOrdersOnly() {
        orderMap.putAll(Map.of(anOrderId()
                        .withId(1L)
                        .build(), marketplace.repository.entity.Order.OrderBuilder.anOrder()
                        .withType(SELL)
                        .withEvents(new ArrayList<>() {
                            {
                                add(anEvent()
                                        .withEventType(LIVE)
                                        .build());
                            }
                        })
                        .withUserId("user1")
                        .build()

                , anOrderId()
                        .withId(2L)
                        .build(), marketplace.repository.entity.Order.OrderBuilder.anOrder()
                        .withType(BUY)
                        .withEvents(new ArrayList<>() {
                            {
                                add(anEvent()
                                        .withEventType(LIVE)
                                        .build());
                            }
                        })
                        .withUserId("user2")
                        .build()

                , anOrderId()
                        .withId(3L)
                        .build(), marketplace.repository.entity.Order.OrderBuilder.anOrder()
                        .withType(BUY)
                        .withEvents(new ArrayList<>() {
                            {
                                add(anEvent()
                                        .withEventType(LIVE)
                                        .build());
                                add(anEvent()
                                        .withEventType(CANCEL)
                                        .build());
                            }
                        })
                        .build()
                , anOrderId()
                        .withId(4L)
                        .build(), marketplace.repository.entity.Order.OrderBuilder.anOrder()
                        .withType(SELL)
                        .withEvents(new ArrayList<>() {
                            {
                                add(anEvent()
                                        .withEventType(LIVE)
                                        .build());
                                add(anEvent()
                                        .withEventType(CANCEL)
                                        .build());
                            }
                        })
                        .build()));


        final var orderRepository = new OrderRepositoryInMemory(orderMap);
        final var liveOrders = orderRepository.getLiveOrders();

        assertThat(liveOrders).containsExactlyInAnyOrder(anOrder()
                        .withType(SELL)
                        .withUserId("user1")
                        .build()
                , anOrder()
                        .withType(BUY)
                        .withUserId("user2")
                        .build());
    }
}