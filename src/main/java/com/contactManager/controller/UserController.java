package com.contactManager.controller;

import com.contactManager.config.Constant;
import com.contactManager.entities.Contact;
import com.contactManager.entities.User;
import com.contactManager.helper.Message;
import com.contactManager.repository.ContactRepo;
import com.contactManager.repository.UserRepo;
import com.contactManager.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FileService fileService;

    @Autowired
    private ContactRepo contactRepo;

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

    @RequestMapping("/show-contacts/{page}")
    public String showContact(
            @PathVariable("page") Integer page,
            Model model,
            Principal principal
    ){
        page = page - 1;
        String email = principal.getName();
        User user = this.userRepo.getUserByUserName(email);
        Pageable pageable = PageRequest.of(page, Constant.DEFAULT_PAGE_SIZE);
        Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(),pageable);
        model.addAttribute("contacts",contacts);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",contacts.getTotalPages());
        model.addAttribute("title","View Contacts");
        return "normal/show_contacts";
    }
}
