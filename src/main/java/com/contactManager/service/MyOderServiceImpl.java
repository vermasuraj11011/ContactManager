package com.contactManager.service;

import com.contactManager.entities.MyOrder;
import com.contactManager.entities.User;
import com.contactManager.repository.MyOrderRepo;
import com.contactManager.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyOderServiceImpl implements MyOrderService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MyOrderRepo myOrderRepo;

    @Override
    public MyOrder create(MyOrder myOrder) {
        return this.myOrderRepo.save(myOrder);
    }

    @Override
    public MyOrder getOrder(Integer id) throws Exception {
        return this.myOrderRepo.findById(id).orElseThrow(() -> new Exception("Order not found with id: " + id));
    }

    @Override
    public MyOrder updateOrder(MyOrder myOrder) {
        return this.myOrderRepo.save(myOrder);
    }

    @Override
    public List<MyOrder> getOrders(User user) {
        return this.myOrderRepo.findByUser(user);
    }

    @Override
    public MyOrder findByOrderId(String orderId) {
        return this.myOrderRepo.findByRazorOrderId(orderId);
    }
}
