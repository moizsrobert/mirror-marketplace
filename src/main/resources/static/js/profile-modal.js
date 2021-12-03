const profileFormIdArray = [
    'personalInfoForm',
    'passwordForm',
    'phoneNumberForm',
    'shippersAddressForm',
    'deleteAccountForm'
];

const profileClearFormIdArray = [
    'clearPersonalInfoForm',
    'clearPhoneNumberForm',
    'clearShippersAddressForm'
];

let profileSuccess = $("#profile-success");
let profileError = $("#profile-error");

function showProfileError(message) {
    hideProfileMessages();
    profileError.removeClass('d-none');
    profileError.html(message);
}

function showProfileSuccess(message) {
    hideProfileMessages();
    profileSuccess.removeClass('d-none');
    profileError.html(message);
}

function hideProfileMessages() {
    profileSuccess.addClass('d-none');
    profileError.addClass('d-none');
    profileSuccess.html();
    profileError.html();
}

function showProfileForm(chosenFormId) {
    hideProfileMessages();

    $.get("/profile", function (user) {
        $("#display-name").val(user.displayName);
        $("#first-name").val(user.firstName);
        $("#last-name").val(user.lastName);
        $("#old-password").val('');
        $("#new-password").val('');
        $("#confirm-password").val('');
        $("#phone-number").val(user.phoneNumber);
        $("#country").val(user.country);
        $("#city").val(user.city);
        $("#delete-password").val('');
    });

    profileFormIdArray.forEach(formId => {
        let form = document.getElementById(formId);
        let button = document.getElementById(formId.replace("Form", ""));

        if (formId !== chosenFormId) {
            form.classList.remove('d-flex');
            form.classList.add('d-none');
            button.classList.remove('btn-primary');
            button.classList.remove('btn-danger');
            if (formId === 'deleteAccountForm')
                button.classList.add('btn-outline-danger');
            else
                button.classList.add('btn-outline-primary');
        } else {
            form.classList.remove('d-none');
            form.classList.add('d-flex');
            button.classList.remove('btn-outline-primary');
            button.classList.remove('btn-outline-danger');
            if (formId === 'deleteAccountForm')
                button.classList.add('btn-danger');
            else
                button.classList.add('btn-primary');
        }
    });
}

profileClearFormIdArray.concat(profileFormIdArray).forEach(formId => {
    $(function () {
        $("#" + formId).on("submit", function (e) {
            e.preventDefault();
            $.ajax({
                url: $(this).attr("action"),
                type: 'POST',
                data: $(this).serialize(),
                success: function () {
                    if (formId === 'deleteAccountForm') {
                        window.location = '/api/login?account_deleted';
                        stop();
                    } else {
                        let formToShow = formId.replace('clear', '');
                        formToShow = formToShow.charAt(0).toLowerCase() + formToShow.slice(1);
                        showProfileForm(formToShow);
                        showProfileSuccess("Changes saved!");
                    }
                }, error: function (xhr, status, error) {
                    if (xhr.status && xhr.status === 400)
                        showProfileError(xhr.responseText);
                    else
                        showProfileError("Something went wrong!");
                }
            });
        });
    });
});