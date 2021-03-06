package org.clickandcollect.consumer.repository;

import org.clickandcollect.model.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByRestaurantId(Long restaurantId);
    Optional<Menu> findMenuByIdAndRestaurantId(Long menuId, Long restaurantId);
}
