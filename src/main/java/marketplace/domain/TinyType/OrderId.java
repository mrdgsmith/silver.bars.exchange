package marketplace.domain.TinyType;

import java.util.Objects;

public class OrderId {
    private final Long id;

    public OrderId(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "OrderId{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(id, orderId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static final class OrderIdBuilder {
        private Long id;

        private OrderIdBuilder() {
        }

        public static OrderIdBuilder anOrderId() {
            return new OrderIdBuilder();
        }

        public OrderIdBuilder withId(final Long id) {
            this.id = id;
            return this;
        }

        public OrderId build() {
            return new OrderId(id);
        }
    }
}
