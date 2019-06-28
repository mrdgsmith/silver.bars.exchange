package marketplace.service;

import marketplace.domain.Order;
import marketplace.domain.OrderBoard;
import marketplace.domain.OrderDisplay;
import marketplace.domain.OrderType;
import marketplace.domain.TinyType.OrderId;
import marketplace.repository.OrderRepository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static java.lang.Runtime.getRuntime;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.*;
import static marketplace.domain.Event.CANCEL;
import static marketplace.domain.OrderBoard.OrderBoardBuilder.anOrderBoard;
import static marketplace.domain.OrderDisplay.OrderDisplayBuilder.anOrderDisplay;
import static marketplace.domain.OrderType.BUY;
import static marketplace.domain.OrderType.SELL;

public class OrderManagementService {
    private final OrderRepository orderRepository;

    public OrderManagementService(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private static List<OrderDisplay> createDisplayOrders(final ConcurrentMap<OrderType, List<Order>> orders
            , final OrderType orderType
            , final Comparator<OrderDisplay> orderDisplayComparator) {
        return orders.get(orderType).stream()
                .collect(groupingBy(Order::getPricePerKilogram, toList()))
                .entrySet().stream()
                .map(entry -> anOrderDisplay()
                        .withPricePerKilogram(entry.getKey())
                        .withQuantity(entry.getValue().stream()
                                .map(Order::getQuantity)
                                .reduce(Double::sum).orElse(null))
                        .withType(orderType)
                        .build())
                .sorted(orderDisplayComparator)
                .collect(toUnmodifiableList());
    }

    public OrderId registerOrder(final Order order) {
        return orderRepository.save(order);
    }

    public void cancelOrder(final OrderId orderId) {
        orderRepository.createEvent(orderId, CANCEL);
    }

    public OrderBoard getOrderBoard() throws InterruptedException, ExecutionException {
        final var orders = orderRepository.getLiveOrders().parallelStream()
                .collect(groupingByConcurrent(Order::getType));

        final Callable<List<OrderDisplay>> getBuyOrders = () -> createDisplayOrders(orders, BUY
                , (orderDisplay1, orderDisplay2) -> orderDisplay2.getPricePerKilogram().getPrice()
                        .compareTo(orderDisplay1.getPricePerKilogram().getPrice()));

        final Callable<List<OrderDisplay>> getSellOrders = () -> createDisplayOrders(orders, SELL
                , Comparator.comparing(orderDisplay -> orderDisplay.getPricePerKilogram().getPrice()));

        var executorService = newFixedThreadPool(getRuntime().availableProcessors());
        final var processBuyOrders = executorService.submit(getBuyOrders);
        final var processSellOrders = executorService.submit(getSellOrders);
        return anOrderBoard()
                .withBuyOrders(processBuyOrders.get())
                .withSellOrders(processSellOrders.get())
                .build();
    }
}