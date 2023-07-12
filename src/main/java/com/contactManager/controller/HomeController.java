package com.contactManager.controller;

import com.contactManager.config.Constant;
import com.contactManager.entities.EmailMessage;
import com.contactManager.entities.User;
import com.contactManager.helper.Message;
import com.contactManager.payload.UserDto;
import com.contactManager.repository.UserRepo;
import com.contactManager.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmailService emailService;

    private final Logger LOGGER = LoggerFactory.getLogger(HomeController.class.getName());

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - This is Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - This is Contact Manager");
        return "about";
    }

    @RequestMapping("/signin")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        return "login";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Signup Page");
        model.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping(value = "do_register", method = RequestMethod.POST)
    public String registerUser(
            @Valid @ModelAttribute("user") User user, BindingResult result,
            @RequestParam(value = "agreement", defaultValue = "false") Boolean agreement,
            Model model,
            HttpSession session
    ) {
        // TODO: 04/07/23 (throw error message when agreement is false)
        // TODO: 05/07/23 (server side validation on the template of signup for other field similar to name) 

        try {
            if (!agreement) {
                throw new Exception("user has not accepted the agreement");
            }

            if (result.hasErrors()) {
                System.out.println(result);
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole(Constant.ROLE_USER);
            user.setEnable(true);
            user.setImageUrl("default.png");
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));

            User savedUser = this.userRepo.save(user);

            Boolean isEmailSend = sendEmail(new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail()), null, null);

            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("User added successfully", "alert-success"));
            return "signup";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message",
                    new Message("Something went wrong " + e.getMessage(), "alert-danger"));
            return "signup";
        }
    }

    private Boolean sendEmail(
            UserDto userDot,
            List<MultipartFile> attachments,
            List<String> attachmentPath) {
        EmailMessage message = new EmailMessage();
        message.setSubject("Welcome to Contact Manager");
        message.setTo(userDot.getEmail());
        message.setSubject("Welcome to Contact Manager " + userDot.getName());

        String welcomeTemplate = Constant.PATH_TO_TEMPLATE + "success_login_email_template.html";
        User user = this.userRepo.getUserByUserName(userDot.getEmail());

//            send welcome email to the user
        return this.emailService.sendMail(user, message, attachments, attachmentPath, welcomeTemplate);
    }
}
