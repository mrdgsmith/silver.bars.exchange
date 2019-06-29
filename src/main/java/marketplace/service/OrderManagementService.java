package marketplace.service;

import marketplace.domain.Order;
import marketplace.domain.OrderBoard;
import marketplace.domain.OrderDisplay;
import marketplace.domain.OrderType;
import marketplace.domain.TinyType.Money;
import marketplace.domain.TinyType.OrderId;
import marketplace.repository.OrderRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static java.lang.Runtime.getRuntime;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.*;
import static marketplace.domain.EventType.CANCEL;
import static marketplace.domain.OrderBoard.OrderBoardBuilder.anOrderBoard;
import static marketplace.domain.OrderDisplay.OrderDisplayBuilder.anOrderDisplay;
import static marketplace.domain.OrderType.BUY;
import static marketplace.domain.OrderType.SELL;

public class OrderManagementService {
    private final OrderRepository orderRepository;

    public OrderManagementService(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private static List<OrderDisplay> createDisplayOrders(final Map<OrderType, List<Order>> orders
            , final OrderType orderType
            , final Comparator<OrderDisplay> orderDisplayComparator) {
        return orders.getOrDefault(orderType, emptyList()).stream()
                .collect(groupingBy(Order::getPricePerKilogram, toList()))
                .entrySet().stream()
                .map(getEntryOrderDisplayFunction(orderType))
                .sorted(orderDisplayComparator)
                .collect(toUnmodifiableList());
    }

    private static Function<Map.Entry<Money, List<Order>>, OrderDisplay> getEntryOrderDisplayFunction(final OrderType orderType) {
        return entry -> anOrderDisplay()
                .withPricePerKilogram(entry.getKey())
                .withQuantity(entry.getValue().stream()
                        .map(Order::getQuantity)
                        .reduce(Double::sum).orElse((double) 0L))
                .withType(orderType)
                .build();
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
                , comparing(OrderDisplay::getPricePerKilogram).reversed());

        final Callable<List<OrderDisplay>> getSellOrders = () -> createDisplayOrders(orders, SELL
                , comparing(OrderDisplay::getPricePerKilogram));

        final var executorService = newFixedThreadPool(getRuntime().availableProcessors());
        final var processBuyOrders = executorService.submit(getBuyOrders);
        final var processSellOrders = executorService.submit(getSellOrders);
        return anOrderBoard()
                .withBuyOrders(processBuyOrders.get())
                .withSellOrders(processSellOrders.get())
                .build();
    }
}