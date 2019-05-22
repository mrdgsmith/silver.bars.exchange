package marketplace.domain.TinyType;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.BigDecimal.valueOf;

public class Money {
    private final BigDecimal price;

    private Money(final BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Money{" +
                "price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(price, money.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }

    public static final class MoneyBuilder {
        private BigDecimal price;

        private MoneyBuilder() {
        }

        public static MoneyBuilder aMoney() {
            return new MoneyBuilder();
        }

        public MoneyBuilder withPrice(final Long price) {
            this.price = valueOf(price);
            return this;
        }

        public Money build() {
            return new Money(price);
        }
    }
}