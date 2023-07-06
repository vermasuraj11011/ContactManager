package com.contactManager.controller;

import com.contactManager.config.Constant;
import com.contactManager.entities.Contact;
import com.contactManager.entities.User;
import com.contactManager.helper.Message;
import com.contactManager.repository.UserRepo;
import com.contactManager.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FileService fileService;

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
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    @RequestMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(
            @ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile imageFile,
            Principal principal,
            HttpSession session
    ) {
        try {

            if (imageFile.isEmpty()) {
                System.out.println("Image is empty");

            } else {
                String fileName = this.fileService.uploadImage(Constant.PATH_SAVE_IMAGE, imageFile);
                contact.setImageUrl(fileName);
                System.out.println("Image is uploaded");
            }

            String name = principal.getName();
            User user = this.userRepo.getUserByUserName(name);
            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepo.save(user);
            System.out.println("Contact added ");
            session.setAttribute("message",new Message("Success, Contact is added.","success"));

        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            session.setAttribute("message",new Message("Error! Contact does not added.","error"));
            e.printStackTrace();
        }
        return "normal/add_contact_form";
    }

    @RequestMapping("/show-contacts")
    public String showContact(Model model){
        return "normal/show_contacts";
    }
}
