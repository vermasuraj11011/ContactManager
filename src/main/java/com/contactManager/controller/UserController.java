package com.contactManager.controller;

import com.contactManager.entities.Contact;
import com.contactManager.entities.User;
import com.contactManager.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @ModelAttribute
    private void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        User user = this.userRepo.getUserByUserName(userName);
        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashBoard(
            Model model,
            Principal principal
    ) {
//        addCommonData(model, principal);
        model.addAttribute("title","User Dashboard");
        return "normal/user_dashboard";
    }

    @RequestMapping("/add-contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }
}
