package marketplace.domain;

import marketplace.domain.TinyType.Money;

import java.util.Objects;

public class OrderDisplay {
    private final Double quantity;
    private final Money pricePerKilogram;
    private final OrderType type;

    private OrderDisplay(final Double quantity, final Money pricePerKilogram, final OrderType type) {
        this.quantity = quantity;
        this.pricePerKilogram = pricePerKilogram;
        this.type = type;
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

    @Override
    public String toString() {
        return "OrderDisplay{" +
                "quantity=" + quantity +
                ", pricePerKilogram=" + pricePerKilogram +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDisplay that = (OrderDisplay) o;
        return Objects.equals(quantity, that.quantity) &&
                Objects.equals(pricePerKilogram, that.pricePerKilogram) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, pricePerKilogram, type);
    }

    public static final class OrderDisplayBuilder {
        private Double quantity;
        private Money pricePerKilogram;
        private OrderType type;

        private OrderDisplayBuilder() {
        }

        public static OrderDisplayBuilder anOrderDisplay() {
            return new OrderDisplayBuilder();
        }

        public OrderDisplayBuilder withQuantity(final Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderDisplayBuilder withPricePerKilogram(final Money pricePerKilogram) {
            this.pricePerKilogram = pricePerKilogram;
            return this;
        }

        public OrderDisplayBuilder withType(final OrderType type) {
            this.type = type;
            return this;
        }

        public OrderDisplay build() {
            return new OrderDisplay(quantity, pricePerKilogram, type);
        }
    }
}