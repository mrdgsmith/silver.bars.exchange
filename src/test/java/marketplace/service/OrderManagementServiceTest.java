package marketplace.service;

import marketplace.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

import static java.util.List.of;
import static marketplace.domain.EventType.CANCEL;
import static marketplace.domain.Order.OrderBuilder.anOrder;
import static marketplace.domain.OrderDisplay.OrderDisplayBuilder.anOrderDisplay;
import static marketplace.domain.OrderType.BUY;
import static marketplace.domain.OrderType.SELL;
import static marketplace.domain.TinyType.Money.MoneyBuilder.aMoney;
import static marketplace.domain.TinyType.OrderId.OrderIdBuilder.anOrderId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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

        var id = 73537876L;
        when(orderRepository.save(order)).thenReturn(anOrderId()
                .withId(id)
                .build());

        var orderId = orderManagementServiceUnderTest.registerOrder(order);

        assertThat(orderId).isEqualTo(anOrderId()
                .withId(id)
                .build());

        verify(orderRepository).save(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Cancel an existing order")
    void cancelAnRegisteredOrder() {
        var orderId = anOrderId()
                .withId(73537876L)
                .build();
        orderManagementServiceUnderTest.cancelOrder(orderId);

        verify(orderRepository).createEvent(orderId, CANCEL);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Display buy orders for unique price in order of highest price")
    void displayCurrentOrderBoardForBuyWithOrderedOfHighestPrice() throws ExecutionException, InterruptedException {
        when(orderRepository.getLiveOrders()).thenReturn(of(anOrder()
                        .withUserId("user4")
                        .withQuantity(5.8)
                        .withPricePerKilogram(aMoney()
                                .withPrice(582L)
                                .build())
                        .withType(BUY)
                        .build()
                , anOrder()
                        .withUserId("user1")
                        .withQuantity(4.8)
                        .withPricePerKilogram(aMoney()
                                .withPrice(234L)
                                .build())
                        .withType(BUY)
                        .build()
                , anOrder()
                        .withUserId("user2")
                        .withQuantity(5.8)
                        .withPricePerKilogram(aMoney()
                                .withPrice(582L)
                                .build())
                        .withType(BUY)
                        .build()
                , anOrder()
                        .withUserId("user1")
                        .withQuantity(8.4)
                        .withPricePerKilogram(aMoney()
                                .withPrice(646L)
                                .build())
                        .withType(SELL)
                        .build()));

        var orderBoard = orderManagementServiceUnderTest.getOrderBoard();
        assertThat(orderBoard.getBuyOrders()).containsExactly(anOrderDisplay()
                        .withQuantity(11.6)
                        .withPricePerKilogram(aMoney()
                                .withPrice(582L)
                                .build())
                        .withType(BUY)
                        .build()
                , anOrderDisplay()
                        .withQuantity(4.8)
                        .withPricePerKilogram(aMoney()
                                .withPrice(234L)
                                .build())
                        .withType(BUY)
                        .build());

        verify(orderRepository).getLiveOrders();
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Display sell orders for unique price in order of highest price")
    void displayCurrentOrderBoardForSellWithOrderedOfLowestPrice() throws ExecutionException, InterruptedException {
        when(orderRepository.getLiveOrders()).thenReturn(of(anOrder()
                        .withUserId("user1")
                        .withQuantity(8.4)
                        .withPricePerKilogram(aMoney()
                                .withPrice(582L)
                                .build())
                        .withType(SELL)
                        .build()
                , anOrder()
                        .withUserId("user4")
                        .withQuantity(5.8)
                        .withPricePerKilogram(aMoney()
                                .withPrice(582L)
                                .build())
                        .withType(SELL)
                        .build()
                , anOrder()
                        .withUserId("user1")
                        .withQuantity(3.1)
                        .withPricePerKilogram(aMoney()
                                .withPrice(789L)
                                .build())
                        .withType(SELL)
                        .build()));

        var orderBoard = orderManagementServiceUnderTest.getOrderBoard();
        assertThat(orderBoard.getSellOrders()).containsExactly(anOrderDisplay()
                        .withQuantity(14.2)
                        .withPricePerKilogram(aMoney()
                                .withPrice(582L)
                                .build())
                        .withType(SELL)
                        .build()
                , anOrderDisplay()
                        .withQuantity(3.1)
                        .withPricePerKilogram(aMoney()
                                .withPrice(789L)
                                .build())
                        .withType(SELL)
                        .build()
        );

        verify(orderRepository).getLiveOrders();
        verifyNoMoreInteractions(orderRepository);
    }
}