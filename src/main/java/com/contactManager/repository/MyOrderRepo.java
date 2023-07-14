package com.contactManager.repository;

import com.contactManager.entities.MyOrder;
import com.contactManager.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyOrderRepo extends JpaRepository<MyOrder, Integer> {

    List<MyOrder> findByUser(User user);

    MyOrder findByRazorOrderId(String razorOrderId);

}
