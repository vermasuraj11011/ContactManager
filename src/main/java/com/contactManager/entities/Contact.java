package com.contactManager.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;
    private String name;
    private String secondName;
    private String email;
    private String phoneNo;
    private String work;
    private String imageUrl;
    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}

