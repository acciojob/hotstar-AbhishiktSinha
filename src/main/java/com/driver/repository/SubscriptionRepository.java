package com.driver.repository;

import com.driver.model.Subscription;
import com.driver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {

    List<Subscription> findByUser(User user);

    @Query(value = "select * from subscription where user_id =: userId", nativeQuery = true)
    List<Subscription> findByUserId(@Param("userId") int userId);

}
