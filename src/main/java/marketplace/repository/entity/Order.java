package marketplace.repository.entity;

import marketplace.domain.Event;
import marketplace.domain.OrderType;
import marketplace.domain.TinyType.Money;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.synchronizedList;
import static java.util.List.copyOf;

public class Order {

    private final String userId;
    private final Double quantity;
    private final Money pricePerKilogram;
    private final OrderType type;
    private final List<Event> events;

    private Order(final String userId, final Double quantity, final Money pricePerKilogram, final OrderType type
            , final List<Event> events) {
        this.userId = userId;
        this.quantity = quantity;
        this.pricePerKilogram = pricePerKilogram;
        this.type = type;
        this.events = synchronizedList(events);
    }

    public String getUserId() {
        return userId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Money getPricePerKilogram() {
        return pricePerKilogram;
    }

    public OrderType getType() {
        return type;
    }

    public List<Event> getEvents() {
        return copyOf(events);
    }

    public void addEvent(final Event event) {
        events.add(event);
    }

    @Override
    public String toString() {
        return "Order{" +
                "userId='" + userId + '\'' +
                ", quantity=" + quantity +
                ", pricePerKilogram=" + pricePerKilogram +
                ", type=" + type +
                ", events=" + events +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(userId, order.userId) &&
                Objects.equals(quantity, order.quantity) &&
                Objects.equals(pricePerKilogram, order.pricePerKilogram) &&
                type == order.type &&
                Objects.equals(events, order.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, quantity, pricePerKilogram, type, events);
    }

    public static final class OrderBuilder {
        private String userId;
        private Double quantity;
        private Money pricePerKilogram;
        private OrderType type;
        private List<Event> events;

        private OrderBuilder() {
        }

        public static OrderBuilder anOrder() {
            return new OrderBuilder();
        }

        public OrderBuilder withUserId(final String userId) {
            this.userId = userId;
            return this;
        }

        public OrderBuilder withQuantity(final Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderBuilder withPricePerKilogram(final Money pricePerKilogram) {
            this.pricePerKilogram = pricePerKilogram;
            return this;
        }

        public OrderBuilder withType(final OrderType type) {
            this.type = type;
            return this;
        }

        public OrderBuilder withEvents(final List<Event> events) {
            this.events = events;
            return this;
        }

        public Order build() {
            return new Order(userId, quantity, pricePerKilogram, type, events);
        }
    }
}
