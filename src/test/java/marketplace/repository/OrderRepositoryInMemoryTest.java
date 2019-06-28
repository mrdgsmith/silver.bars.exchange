package marketplace.repository;

import marketplace.domain.Order;
import marketplace.domain.TinyType.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static marketplace.domain.Order.OrderBuilder.anOrder;
import static marketplace.domain.OrderType.SELL;
import static marketplace.domain.TinyType.Money.MoneyBuilder.aMoney;
import static marketplace.domain.TinyType.OrderId.OrderIdBuilder.anOrderId;
import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryInMemoryTest {

    private OrderRepository orderRepository;
    private ConcurrentHashMap<OrderId, Order> orderMap;

    @BeforeEach
    void setUp() {
        orderMap = new ConcurrentHashMap<>();
        orderRepository = new OrderRepositoryInMemory(orderMap);
    }

    @Test
    @DisplayName("Add order to database")
    void addOrderToDatabase() {
        var orderId = orderRepository.save(anOrder()
                .withType(SELL)
                .withQuantity(574D)
                .withPricePerKilogram(aMoney()
                        .withPrice(4564L)
                        .build())
                .withUserId("user1")
                .build());
        assertThat(orderMap).containsOnlyKeys(orderId).containsValue(anOrder()
                .withType(SELL)
                .withQuantity(574D)
                .withPricePerKilogram(aMoney()
                        .withPrice(4564L)
                        .build())
                .withUserId("user1")
                .build());
    }

    @Test
    @DisplayName("Add order to database with existing orders")
    void addOrderToDatabaseWithExistingOrders() {
        OrderId orderId1 = anOrderId()
                .withId(1L)
                .build();
        orderMap.put(orderId1, anOrder()
                .withType(SELL)
                .build());

        final var orderRepository = new OrderRepositoryInMemory(orderMap);


        var orderId = orderRepository.save(anOrder()
                .withType(SELL)
                .withQuantity(574D)
                .withPricePerKilogram(aMoney()
                        .withPrice(4564L)
                        .build())
                .withUserId("user1")
                .build());
        assertThat(orderId).isEqualTo(anOrderId()
                .withId(2L)
                .build());
        assertThat(orderMap).containsOnlyKeys(orderId1, orderId).containsValues(anOrder()
                .withType(SELL)
                .withQuantity(574D)
                .withPricePerKilogram(aMoney()
                        .withPrice(4564L)
                        .build())
                .withUserId("user1")
                .build(), anOrder()
                .withType(SELL)
                .build());
    }
}