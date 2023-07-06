package com.contactManager.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Size(min = 3, max = 20, message = "Name should  be between 3 and 20 character")
    private String name;

    @Column(unique = true)
    @Email
    private String email;

    @Size(min = 5, message = "password should be more than 5 character")
    private String password;

    @Column(length = 1000)
    private String about;
    private String role;
    private Boolean enable;
    private String imageUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contact> contacts = new ArrayList<>();

}
