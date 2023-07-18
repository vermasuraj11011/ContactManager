# Contact Manager

It is a contact management system were user can manage there contacts and view it. It is a fullstack web project
containing both frontend and backend.

## What is the use of this Repo

1. User **Form** base login.
2. **CRUD** operation on **User** and **Contact**.
3. **Search** and **pagination** on view contact page.
4. Implement AWS **Queue**, **Lambda** and **SES**.
5. Dynamic web page using **Thymeleaf**.
6. Send welcoming **Email** to user.
7. **Razorpay** Payment integration.
8. **Tiny MCE** HTML editor.
9. **Pdf** generation on sample html.
10. **MySql** Database.
11. Adding **Image**

## TechStack used

- SpringBoot (Spring Security, JavaX, OpenPdf, JPA)
- Thymeleaf
- AWS (Queue, Lambda, SES and CloudWatch)
- Html, CSS and JavaScript
- Python (Lambda Script)

## Integration

- Razorpay Payment Gateway
- Tiny MCE editor
- Bootstrap
- SweetAlert

## Application design

![MVC_View](./src/main/resources/static/image/project_view/contact_manager_mvc.png)

## Features

- User registration and login.
- Send welcome email to user.
- Accept Donation from the user.
- User can change password and profile picture.
- View, Add, Update and Delete contacts.
- Generate PDF.

## URL

base url <br>
http://localhost:8080

##### Home

| Action            | Controller | Method | Url          | view        |
|-------------------|------------|--------|--------------|-------------|
| Home page         | home       | GET    | /            | home.html   |
| About page        | home       | GET    | /about       | about.html  |
| Registration Page | home       | GET    | /signup      | signup.html |
| Login  page       | home       | GET    | /signin      | login.html  |
| Register User     | home       | POST   | /do_register | signup.html |

##### Forgot Password

| Action          | Controller | Method | Url              | success view              | failure view    | failure reason |
|-----------------|------------|--------|------------------|---------------------------|-----------------|----------------|
| Forgot Password | home       | GET    | /forgot          | email_form.html           | -               |                |
| Send OTP        | home       | POST   | /send-otp        | verify-otp.html           | email_form.html | wrong email    |
| Verify OTP      | home       | POST   | //verify-otp     | password_change_form.html | verify-otp.html | wrong otp      |
| New Password    | home       | DELETE | /change-password | login.html                |                 |                |

<div style="background-color: #d3f5d3; border: 1px solid #0c8900; padding: 10px; border-radius: 5px; color: #006600;">
All the above URL are public url.
</div>




## Resources

- [SpringBoot Project Structure](https://start.spring.io/)
- [Maven Repository](https://mvnrepository.com/artifact/org.springframework)
- [JDK-11](https://www.oracle.com/in/java/technologies/javase/jdk11-archive-downloads.html)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine.

- One way is to execute the `main` method in the `src.com.course.springrest.SpringrestApplication`
  class from your IDE.


- Or you can use the command given below while in the target project:

```shell
mvn spring-boot:run
```

