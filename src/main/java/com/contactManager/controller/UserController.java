package com.contactManager.controller;

import com.contactManager.config.Constant;
import com.contactManager.entities.Contact;
import com.contactManager.entities.MyOrder;
import com.contactManager.entities.User;
import com.contactManager.helper.Message;
import com.contactManager.repository.ContactRepo;
import com.contactManager.repository.UserRepo;
import com.contactManager.service.FileService;
import com.contactManager.service.MyOrderService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FileService fileService;

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MyOrderService myOrderService;

    @Value("${razor.key}")
    private String razorKey;

    @Value("${razor.secret}")
    private String razorValue;

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
                contact.setImageUrl(Constant.DEFAULT_CONTACT_IMAGE);
            } else {
                String fileName = this.fileService.uploadImage(imageFile);
                contact.setImageUrl(fileName);
                System.out.println("Image is uploaded");
            }

            String name = principal.getName();
            User user = this.userRepo.getUserByUserName(name);
            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepo.save(user);
            System.out.println("Contact added ");
            session.setAttribute("message", new Message("Success, Contact is added.", "success"));

        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            session.setAttribute("message", new Message("Error! Contact does not added.", "error"));
            e.printStackTrace();
        }
        return "normal/add_contact_form";
    }

    @RequestMapping("/show-contacts/{page}")
    public String showContact(
            @PathVariable("page") Integer page,
            Model model,
            Principal principal
    ) {
        page = page - 1;
        String email = principal.getName();
        User user = this.userRepo.getUserByUserName(email);
        Pageable pageable = PageRequest.of(page, Constant.DEFAULT_PAGE_SIZE);
        Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(), pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        model.addAttribute("title", "View Contacts");
        return "normal/show_contacts";
    }

    @GetMapping("/contact/{contactId}")
    public String showContactInfo(
            @PathVariable("contactId") Integer cId,
            Model model,
            Principal principal
    ) {
        System.out.println(cId);
        Contact contact = this.contactRepo.findById(cId).get();
        String userName = principal.getName();
        User user = this.userRepo.getUserByUserName(userName);
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }
        return "normal/contact_detail";
    }

    @GetMapping("/delete/{cId}")
    private String deleteContact(
            @PathVariable("cId") Integer cId,
            Model model,
            HttpSession session
    ) {
        // TODO: 07/07/23 handel .get with proper exception and check if contact belongs to the same user
        Contact contact = this.contactRepo.findById(cId).get();
        contact.setUser(null);
        this.contactRepo.delete(contact);
        session.setAttribute("message", new Message("Contact deleted successfully", "success"));
        return "redirect:/user/show-contacts/1";
    }

    //    update contact details form opens
    @PostMapping("/update-contact/{cId}")
    public String showUpdateContactForm(
            @PathVariable("cId") Integer cId,
            Model model
    ) {
        Contact contact = this.contactRepo.findById(cId).get();
        model.addAttribute("contact", contact);
        model.addAttribute("title", "Update Contact");
        return "/normal/update_contact";
    }

    //    update contact detail
    @PostMapping("/process-update")
    public String updateContact(
            @ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile image,
            HttpSession session,
            Principal principal
    ) {
        try {
            Contact oldContactDetail = this.contactRepo.findById(contact.getCId()).get();
            if (!image.isEmpty()) {
                Boolean isDeleted = this.fileService.deleteImage(oldContactDetail.getImageUrl());
                String imageName = this.fileService.uploadImage(image);
                contact.setImageUrl(imageName);
            } else {
                contact.setImageUrl(oldContactDetail.getImageUrl());
            }
            User user = this.userRepo.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepo.save(contact);
            session.setAttribute("Your contact is updated", "success");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/user/contact/" + contact.getCId();
    }

    //    show user profile view
    @GetMapping("/profile")
    public String userProfileView(Model model) {
        model.addAttribute("title", "Profile");
        return "/normal/profile";
    }

    //    show setting view
    @GetMapping("/setting")
    public String userSetting(Model model) {
        model.addAttribute("title", "setting");
        return "/normal/setting";
    }

    //    reset password
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("oldPassword") String oldPass,
            @RequestParam("newPassword") String newPass,
            HttpSession session,
            Principal principal
    ) {
        String userName = principal.getName();
        User user = this.userRepo.getUserByUserName(userName);
        if (passwordEncoder.matches(oldPass, user.getPassword())) {
            String newPassword = this.passwordEncoder.encode(newPass);
            user.setPassword(newPassword);
            this.userRepo.save(user);
            session.setAttribute("message", new Message("Password changed successfully...", "success"));
        } else {
            session.setAttribute("message", new Message("Old password is wrong", "danger"));
            return "redirect:/user/setting";
        }
        return "redirect:/user/index";
    }

    //  integrating razor payment
    @PostMapping("/create-payment-order")
    @ResponseBody
    public String createOrder(
            @RequestBody Map<String, Object> request,
            Principal principal
    ) throws RazorpayException {
        System.out.println("order executed");
        int amount = Integer.parseInt(request.get("amount").toString());

        RazorpayClient client = new RazorpayClient(this.razorKey, this.razorValue);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount * 100);
        jsonObject.put("currency", "INR");
        jsonObject.put("receipt", "txn_235425");

//        create new order
        Order order = client.orders.create(jsonObject);

        MyOrder myOrder = new MyOrder();
        myOrder.setAmount(order.get("amount"));
        myOrder.setRazorOrderId(order.get("id"));
        myOrder.setPaymentId(null);
        myOrder.setStatus("created");
        myOrder.setUser(this.userRepo.getUserByUserName(principal.getName()));
        myOrder.setReceipt(order.get("receipt"));

        this.myOrderService.create(myOrder);

        System.out.println(order);
        return order.toString();
    }

    @PostMapping("/update_payment")
    public ResponseEntity<?> updateOrder(
            @RequestBody Map<String, Object> data,
            Principal principal
    ) {
        System.out.println(data);
        String orderId = (String) data.get("order_id");
        MyOrder myOrder = this.myOrderService.findByOrderId(orderId);
        myOrder.setPaymentId((String) data.get("payment_id"));
        myOrder.setStatus((String) data.get("status"));

        this.myOrderService.updateOrder(myOrder);
        return ResponseEntity.ok("success");
    }
}
