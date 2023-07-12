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