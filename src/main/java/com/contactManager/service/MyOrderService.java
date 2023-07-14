package com.contactManager.service;

import com.contactManager.entities.MyOrder;
import com.contactManager.entities.User;

import java.util.List;

public interface MyOrderService {

    MyOrder create(MyOrder myOrder);

    MyOrder getOrder(Integer id) throws Exception;

    MyOrder updateOrder(MyOrder myOrder);

    List<MyOrder> getOrders(User user);

    MyOrder findByOrderId(String orderId);
}
