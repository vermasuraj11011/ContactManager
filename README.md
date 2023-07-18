# Contact Manager   <img src="./src/main/resources/static/image/contact_manager_logo.png" display="block" alt="Logo" width="20" height="20">

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

#### MVC Design

![MVC_View](src/main/resources/static/image/project_view/design/contact_manager_mvc.png)

#### Schema Design

![Schema_Design](src/main/resources/static/image/project_view/design/schema-design.png)

## Features

- User registration and login.
- Send welcome email to user.
- Accept Donation from the user.
- User can change password and profile picture.
- View, Add, Update and Delete contacts.
- Generate PDF.

## URL

```text
Base URL:   http://localhost:8080
```

##### Home

| Action            | Controller | Method | Url ("/")    | view         |
|-------------------|------------|--------|--------------|--------------|
| Home page         | home       | GET    | /            | /home.html   |
| About page        | home       | GET    | /about       | /about.html  |
| Registration Page | home       | GET    | /signup      | /signup.html |
| Login  page       | home       | GET    | /signin      | /login.html  |
| Register User     | home       | POST   | /do_register | /signup.html |

##### Forgot Password

| Action          | Controller | Method | Url ("/")        | success view               | failure view     | failure reason |
|-----------------|------------|--------|------------------|----------------------------|------------------|----------------|
| Forgot Password | home       | GET    | /forgot          | /email_form.html           | -                |                |
| Send OTP        | home       | POST   | /send-otp        | /verify-otp.html           | /email_form.html | wrong email    |
| Verify OTP      | home       | POST   | /verify-otp      | /password_change_form.html | /verify-otp.html | wrong otp      |
| New Password    | home       | DELETE | /change-password | /login.html                |                  |                |

`All the above URL are public url, below one is private.`

##### User Api

| Action          | Controller | Method | Url ("/user")    | view ("/normal")     | 
|-----------------|------------|--------|------------------|----------------------|
| User Dashboard  | User       | GET    | /index           | /user_dashboard.html | 
| User profile    | User       | GET    | /profile         | /profile.html        | 
| User setting    | User       | GET    | /setting         | /setting.html        | 
| Change Password | User       | GET    | /change-password | /user_dashboard.html | 

##### Contact Api

| Action                   | Controller | Method | Url ("/user")                | view ("/normal")       | 
|--------------------------|------------|--------|------------------------------|------------------------|
| Add Contact Page         | User       | GET    | /add-contact                 | /add_contact_form.html | 
| Add Contact Detail       | User       | POST   | /process-contact             | /add_contact_form.html | 
| Search Contact           | Search     | GET    | /search/{query}              | -                      | 
| Show List Of Contact     | User       | GET    | /show-contacts/{page_no}     | /show_contacts.html    | 
| Show Single Contact Info | User       | GET    | /contact/{contact_id}        | /contact_detail.html   | 
| Show Update Contact Page | User       | POST   | /update-contact/{contact_id} | /update_contact.html   | 
| Update Contact           | User       | POST   | /process-update              | /contact_detail.html   | 
| Delete Contact           | User       | GET    | /delete/{cId}                | /show_contacts.html    | 

##### Payment Api

| Action                      | Controller | Method | Url ("/user")         |
|-----------------------------|------------|--------|-----------------------|
| Initiate Donation           | User       | POST   | /create-payment-order |
| Update Payment Status in DB | User       | POST   | /create-payment-order |

##### PDF Api

| Action       | Controller | Method | Url ("/user") |
|--------------|------------|--------|---------------|
| Download PDF | PDF        | GET    | /createPdf    |

## Integration

#### RazorPay

Implemented Razorpay integration to accept the donation.

Steps to follow:-

- [Generate API key.](https://razorpay.com/docs/payments/dashboard/account-settings/api-keys/)

- Include razorPay dependency to pom.xml.

```xml
<!-- https://mvnrepository.com/artifact/com.razorpay/razorpay-java -->
<dependency>
    <groupId>com.razorpay</groupId>
    <artifactId>razorpay-java</artifactId>
    <version>1.4.4</version>
</dependency>
```

- Create a new razorpay client

```text
RazorpayClient client = new RazorpayClient(razorKey, razorValue);
```

- Create a JSON object with the following information

```text
JSONObject jsonObject = new JSONObject();
jsonObject.put("amount", amount * 100);
jsonObject.put("currency", "INR");
jsonObject.put("receipt", "txn_235425");
```

- Create a razorpay order from the JSON object. This will initiate a new order to razorpay. You can view this order in
  your razorpay dashboard

```text
Order order = client.orders.create(jsonObject);
```

- Create a client side checkout button which will initiate the payment order. Check startPayment in script.js for better
  understanding<br>
  [Reference](https://razorpay.com/docs/payments/payment-gateway/web-integration/standard/build-integration/#12-integrate-with-checkout-on-client-side)
- Once payment is completed we can save the data to our DB.
- For testing follow this <br>
  [Get card detail](https://razorpay.com/docs/payments/payments/test-card-details/) <br>
  [Get Upi detail](https://razorpay.com/docs/payments/payments/test-upi-details/)

#### AWS SQS

I have used SQS to send email asynchronously.

Steps to follow:

- Create an [Identity](https://docs.aws.amazon.com/ses/latest/dg/creating-identities.html) in the AWS SES and verify it.
- Create a [IAM role](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-user.html) which can be
  access by Lambda and has AmazonSESFullAccess permission.
- Create [Lambda](https://docs.aws.amazon.com/lambda/latest/dg/getting-started.html) in AWS and assign the above role to
  it.
- Use below Python code block.

```python
import json
import boto3

def lambda_handler(event, context):
    client = boto3.client('ses')
    
    for record in event['Records']:
        body = json.loads(record['body'])
        user_email = body['user']['email']
        template = body['template']
        
        subject = "Welcome to our application"
        
        message = {
            "Subject": { "Data": subject },
            "Body": { "Html": { "Data": template } }
        }
        
        response = client.send_email(
            Source='<from_email_id>',
            Destination={ "ToAddresses": [user_email] },
            Message=message
        )
    
    return response
```

- Create SQS in AWS by
  following [this](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/step-create-queue.html)
- Include the below two dependencies for SQS in the pom.xml file.

```html
<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-aws -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-aws</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>

<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-aws-messaging -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-aws-messaging</artifactId>
  <version>2.2.6.RELEASE</version>
</dependency>
```

- Follow AwsSQSService module from services.

## Views

#### Home

![Home](./src/main/resources/static/image/project_view/home/home_view.png)
![About](./src/main/resources/static/image/project_view/home/about_view.png)
![Register](./src/main/resources/static/image/project_view/home/register_view.png)
![Login](./src/main/resources/static/image/project_view/home/login_view.png)

#### User

![Home](./src/main/resources/static/image/project_view/user/user_home_view.png)
![Profile](./src/main/resources/static/image/project_view/user/user_profile_view.png)
![View Contact](./src/main/resources/static/image/project_view/user/all_contact_view.png)
![Add Contact](./src/main/resources/static/image/project_view/user/add_contact_view.png)
![Update Contact](./src/main/resources/static/image/project_view/user/update_contact_view.png)
![Setting](./src/main/resources/static/image/project_view/user/setting_view.png)
![Logout](./src/main/resources/static/image/project_view/user/loggout_successful_view.png)

#### Forgot Password

![Forgot Password](./src/main/resources/static/image/project_view/password_change/fp_email_form.png)
![Enter OTP](./src/main/resources/static/image/project_view/password_change/fp_otp_form.png)
![Wrong OTP](./src/main/resources/static/image/project_view/password_change/fp_wrong_otp.png)
![New Password](./src/main/resources/static/image/project_view/password_change/fp_new_password_form.png)
![Password Reset Successful](./src/main/resources/static/image/project_view/password_change/password_reset_page.png)

#### Payment 
![Donate](./src/main/resources/static/image/project_view/user/user_home_view.png)
![Razor Payment View](./src/main/resources/static/image/project_view/payment/razorpay_payment_method_select_view.png)
![Enter UPI](./src/main/resources/static/image/project_view/payment/razorpay_card_detail_view.png) 
![Razor Success View](./src/main/resources/static/image/project_view/payment/razorepay_success_view.png)
![Success View](./src/main/resources/static/image/project_view/payment/payment_success_view.png)
![Razor Failure View](./src/main/resources/static/image/project_view/payment/rayzorpay_payment_failied_view.png) 
![Failure View](./src/main/resources/static/image/project_view/payment/payment_failed_view.png)

#### Welcome Email
![Welcome Email](./src/main/resources/static/image/project_view/home/welcome_mail.png)


## Resources

- [SpringBoot Project Structure](https://start.spring.io/)
- [Maven Repository](https://mvnrepository.com/artifact/org.springframework)
- [JDK-11](https://www.oracle.com/in/java/technologies/javase/jdk11-archive-downloads.html)
- [BootStrap](https://getbootstrap.com/docs/5.3/getting-started/introduction/)
- [Tiny MCE](https://www.tiny.cloud/docs/tinymce/6/cloud-quick-start/)
- [Create SQS](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/step-create-queue.html)
- [Create Lambda](https://docs.aws.amazon.com/lambda/latest/dg/getting-started.html)
- [Razorpay Integration](https://razorpay.com/docs/payments/payment-gateway/web-integration/standard/build-integration/#12-integrate-with-checkout-on-client-side)


## Running the application locally

There are several ways to run a Spring Boot application on your local machine.

- One way is to execute the `main` method in the `src.main.java.com.contactManager.ContactManagerApplication`
  class from your IDE.


- Or you can use the command given below while in the target project:

```shell
mvn spring-boot:run
```

## 🚀 About Me

### Hi, I'm Suraj Verma! 👋

I'm a full stack developer... 
<br>
`vermasuraj@gmail.com`

[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://surajverma008.netlify.app/) <br>
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/surajverma008/)