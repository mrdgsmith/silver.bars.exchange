package marketplace.domain;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

public class OrderBoard {
    private final List<OrderDisplay> sellOrders;
    private final List<OrderDisplay> buyOrders;

    private OrderBoard(final List<OrderDisplay> sellOrder, final List<OrderDisplay> buyOrders) {
        this.sellOrders = unmodifiableList(sellOrder);
        this.buyOrders = unmodifiableList(buyOrders);
    }

    public List<OrderDisplay> getSellOrders() {
        return unmodifiableList(sellOrders);
    }

    public List<OrderDisplay> getBuyOrders() {
        return unmodifiableList(buyOrders);
    }

    @Override
    public String toString() {
        return "OrderBoard{" +
                "sellOrders=" + sellOrders +
                ", buyOrders=" + buyOrders +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBoard that = (OrderBoard) o;
        return Objects.equals(sellOrders, that.sellOrders) &&
                Objects.equals(buyOrders, that.buyOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellOrders, buyOrders);
    }

    public static final class OrderBoardBuilder {
        private List<OrderDisplay> sellOrders;
        private List<OrderDisplay> buyOrders;

        private OrderBoardBuilder() {
        }

        public static OrderBoardBuilder anOrderBoard() {
            return new OrderBoardBuilder();
        }

        public OrderBoardBuilder withSellOrders(final List<OrderDisplay> sellOrders) {
            this.sellOrders = sellOrders;
            return this;
        }

        public OrderBoardBuilder withBuyOrders(final List<OrderDisplay> buyOrders) {
            this.buyOrders = buyOrders;
            return this;
        }

        public OrderBoard build() {
            return new OrderBoard(sellOrders, buyOrders);
        }
    }
}