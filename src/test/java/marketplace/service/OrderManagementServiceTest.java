package marketplace.service;

import marketplace.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static marketplace.domain.Order.OrderBuilder.anOrder;
import static marketplace.domain.OrderType.BUY;
import static marketplace.domain.TinyType.Money.MoneyBuilder.aMoney;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class OrderManagementServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderManagementService orderManagementServiceUnderTest;

    @BeforeEach
    void setUp() {
        orderManagementServiceUnderTest = new OrderManagementService(orderRepository);
    }

    @Test
    @DisplayName("Register an order on the system")
    void registerOrderToMarket() {

        final var order = anOrder()
                .withUserId("user1")
                .withQuantity(5.8)
                .withPricePerKilogram(aMoney()
                        .withPrice(582L)
                        .build())
                .withType(BUY)
                .build();

        orderManagementServiceUnderTest.registerOrder(order);

        verify(orderRepository).save(order);
        verifyNoMoreInteractions(orderRepository);
    }
}