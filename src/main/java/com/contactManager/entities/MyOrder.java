package com.contactManager.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "razor_id")
    private String razorOrderId;

    private Integer amount;

    private String receipt;

    @Column(name = "payment_id")
    private String paymentId;

    private String status;

    @ManyToOne
    private User user;
}
