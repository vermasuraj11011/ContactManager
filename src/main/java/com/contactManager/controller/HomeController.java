package com.contactManager.controller;

import com.contactManager.config.Constant;
import com.contactManager.entities.User;
import com.contactManager.helper.Message;
import com.contactManager.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

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
                System.out.println(result.toString());
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole(Constant.ROLE_USER);
            user.setEnable(true);
            user.setImageUrl("default.png");
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));

            User userSaved = this.userRepo.save(user);

            System.out.println("agreement " + agreement);
            System.out.println("user " + user.toString());
            System.out.println(model.toString());

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
}
