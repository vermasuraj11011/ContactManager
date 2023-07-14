console.log("this is script file")

const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
}

function deleteContact(cId) {
    swal({
            title: "Are you sure?",
            text: "You want to delete this contact..",
            icon: "warning",
            buttons: true,
            dangerMode: true,
        })
        .then((willDelete) => {
            if (willDelete) {
                window.location = "/user/delete/" + cId;
            }
        });
}

function submitForm(formId) {
    document.getElementById(formId).submit();
}

const search = () => {
    // console.log("searching...")
    let query = $("#search-input").val()

    if (query == "") {
        $(".search-result").hide()
    } else {
        console.log(query)
        let url = `http://localhost:8080/search/${query}`

        fetch(url).then((response) => {
            return response.json()
        }).then((data) => {
            let text = `<div class='list-group'> `
            data.forEach(contact => {
                console.log(contact)
                text += `<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action'> ${contact.name} </a>`
            });
            text += `<div/>`
            $(".search-result").html(text);
            $(".search-result").show()
        })
    }
}

const startPayment = () => {
    console.log("starting payment ....")
    let amount = $("#payment-field").val()
    console.log(amount)
    if (amount == '' || amount == null || amount == 0) {
        alert("amount should be greater that 0")
        return;
    }

    $.ajax({
        url: '/user/create-payment-order',
        data: JSON.stringify({ amount: amount, info: 'order_request' }),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success: function(response) {
            console.log(response)
            if (response.status == "created") {
                let options = {
                    key: "rzp_test_Wi0sHChZ5qqzky",
                    amount: response.amount,
                    currency: "INR",
                    name: "Contact Manager",
                    description: "Donation",
                    image: "https://drive.google.com/file/d/17o6VeaHpZi3B7kXlUfxvgZ_rOsU2fKnJ/view?usp=drive_link",
                    order_id: response.id,
                    handler: function(response) {
                        console.log(response.razorpay_payment_id)
                        console.log(response.razorpay_order_id)
                        console.log(response.razorpay_signature)
                        console.log("payment successfull..")
                        updatePaymentOnServer(
                            response.razorpay_payment_id,
                            response.razorpay_order_id,
                            response.razorpay_signature
                        )
                    },

                    prefill: {
                        "name": "",
                        "email": "",
                        "contact": ""
                    },
                    "notes": {
                        "address": "Contact Manager"
                    },
                    "theme": {
                        "color": "#3399cc"
                    }
                };

                let rzp = new Razorpay(options);
                rzp.on("payment.failed", function(response) {
                    console.log(response.error.code)
                    console.log(response.error.description)
                    console.log(response.error.source)
                    console.log(response.error.step)
                    console.log(response.error.reasone)
                    console.log(response.error.metadata.order_id)
                    console.log(response.error.metadata.payment_id)
                    swal("Oops!", "Payment Failed", "error");
                })
                rzp.open();
            }
        },
        error: function(error) {
            // invoked when error
            console.log(error)
            alert("something went wrong !!")
        }

    })
};


function updatePaymentOnServer(payment_id, order_id, status) {
    $.ajax({
        url: "/user/update_payment",
        data: JSON.stringify({
            payment_id: payment_id,
            order_id: order_id,
            status: status
        }),
        contentType: "application/json",
        type: "POST",
        dataType: "json",
        success: function(success) {
            swal("Thank You!", "Payment Successfull", "success");
        },
        error: function(error) {
            swal("Thank You!", "Payment Successfull, but didn't saved to our server", "success");
        }
    })
}