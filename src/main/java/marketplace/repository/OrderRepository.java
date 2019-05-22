package marketplace.repository;

import marketplace.domain.Order;

public interface OrderRepository {
    void save(final Order order);
}