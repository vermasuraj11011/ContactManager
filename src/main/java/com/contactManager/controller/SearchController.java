package com.contactManager.controller;

import com.contactManager.entities.Contact;
import com.contactManager.entities.User;
import com.contactManager.repository.ContactRepo;
import com.contactManager.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ContactRepo contactRepo;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> searchContact(
        @PathVariable("query") String query,
        Principal principal
    ){
        System.out.println("query "+query);
        User user = this.userRepo.getUserByUserName(principal.getName());
        List<Contact> contacts = this.contactRepo.findByNameContainingAndUser(query,user);
        return ResponseEntity.ok(contacts);
    }
}
