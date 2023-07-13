package com.contactManager.controller;

import com.contactManager.config.Constant;
import com.contactManager.entities.EmailMessage;
import com.contactManager.entities.User;
import com.contactManager.enums.EmailTypes;
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
import java.security.Principal;
import java.util.List;
import java.util.Random;

@Controller
public class HomeController {

    private final Random random = new Random(1000);

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

            UserDto userDto = new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
            EmailMessage message = new EmailMessage();
            message.setTo(userDto.getEmail());
            message.setSubject("Welcome to Contact Manager " + userDto.getName());

            Boolean isEmailSend = sendEmail(
                    savedUser,
                    null,
                    null,
                    Constant.PATH_TO_TEMPLATE + "success_login_email_template.html",
                    message,
                    EmailTypes.REGISTER_SUCCESS_EMAIL);

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
            User user,
            List<MultipartFile> attachments,
            List<String> attachmentPath,
            String template,
            EmailMessage message,
            EmailTypes emailTypes) {
        return this.emailService.sendMail(user, message, attachments, attachmentPath, template);
    }

    //    forget password view
    @RequestMapping("/forgot")
    public String forgetPasswordForm() {
        return "/email_form";
    }

    //    otp page
    @PostMapping("/send-otp")
    public String sendOtp(
            @RequestParam("email") String email,
            HttpSession session
    ) throws InterruptedException {
        System.out.println("email ->" + email);
        int otp = random.nextInt(999999);
        System.out.println("otp ->" + otp);
        User user = null;
        try {
            user = this.userRepo.getUserByUserName(email);
            if (user == null) throw new Exception("Email not register");
            String subject = "Otp received fom Contact Manager";
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
            EmailMessage message = new EmailMessage();
            message.setTo(userDto.getEmail());
            message.setSubject(subject);
            message.setMessage("Otp is " + otp);
            sendEmail(
                    user,
                    null,
                    null,
                    null,
                    message,
                    EmailTypes.SEND_OTP);
        } catch (Exception e) {
            session.setAttribute("message", new Message("Email not register", "danger"));
            return "redirect:/forgot";
        }
        session.setAttribute("otp", otp);
        session.setAttribute("email", user.getEmail());
        session.setAttribute("message", new Message("Otp sent on your email", "success"));
        return "/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam("otp") Integer userOtp,
            HttpSession session
    ) {
        Integer sessionOtp = (int) session.getAttribute("otp");
        if (sessionOtp.equals(userOtp)) {
            return "password_change_form";
        } else {
            session.setAttribute("message", new Message("You have entered wrong otp", "danger"));
            return "verify-otp";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("password") String newPassword,
            HttpSession session
    ) {
        String email = String.valueOf(session.getAttribute("email"));
        User user = this.userRepo.getUserByUserName(email);
        String encodePass = this.passwordEncoder.encode(newPassword);
        user.setPassword(encodePass);
        this.userRepo.save(user);
        session.setAttribute("message", new Message("Password change successfully !!", "success"));
        return "login";
    }
}
