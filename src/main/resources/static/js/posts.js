let user = setUser();

function setUser() {
    $.ajax({
        url: '/profile',
        type: 'GET',
        success: function (data) {
            user = data;
        }
    });
}

const urlParams = new URLSearchParams(window.location.search);
if (urlParams.has("post")) {
    const postId = urlParams.get('post');
    displayPost(postId);
}

const stages = [
    'home',
    'search',
    'displayed-post'
]

const postBoxIdArray = [
    'myPostsBox',
    'savedPostsBox',
    'newPostBox'
];

let postSuccess = $("#post-success");
let postError = $("#post-error");

function showPostError(message) {
    hidePostMessages();
    postError.removeClass('d-none');
    postError.html(message);
}

function showPostSuccess(message) {
    hidePostMessages();
    postSuccess.removeClass('d-none');
    postSuccess.html(message);
}

function hidePostMessages() {
    postSuccess.addClass('d-none');
    postError.addClass('d-none');
    postSuccess.html();
    postError.html();
}

function showPostBox(chosenBoxId) {
    hidePostMessages();

    let box = $("#" + chosenBoxId);

    if (chosenBoxId === 'myPostsBox') {
        box.empty();
        $.get("/posts/my-posts", function (data) {
            if (data.length === 0) {
                let noPostsBox = $("<div class=\"h-100 d-flex flex-column justify-content-center\"></div>");
                let noPostsText = $("<h3>You have no active posts</h3>");
                $('#myPostsBox').append(noPostsBox);
                noPostsBox.append(noPostsText);
            } else showMyPosts(data);
        });
    } else if (chosenBoxId === 'savedPostsBox') {
        box.empty();
        $.get("/posts/saved-posts", function (data) {
            if (data.length === 0) {
                let noPostsBox = $("<div class=\"h-100 d-flex flex-column justify-content-center\"></div>");
                let noPostsText = $("<h3>You have no saved posts</h3>");
                $('#savedPostsBox').append(noPostsBox);
                noPostsBox.append(noPostsText);
            } else showSavedPosts(data);
        });
    } else {
        $('#post-name').val('');
        $('#post-price').val('');
        $('#category').val('NOTHING_SELECTED');
        $('#post-description').val('');
        $('#post-images').val('');
    }

    postBoxIdArray.forEach(boxId => {
        let box = document.getElementById(boxId);
        let button = document.getElementById(boxId.replace("Box", ""));

        if (boxId !== chosenBoxId) {
            box.classList.remove('d-flex');
            box.classList.add('d-none');
            button.classList.remove('btn-primary');
            button.classList.add('btn-outline-primary');
        } else {
            box.classList.remove('d-none');
            box.classList.add('d-flex');
            button.classList.remove('btn-outline-primary');
            button.classList.add('btn-primary');
        }
    });
}

function showMyPosts(data) {
    let box = $('#myPostsBox');

    for (let i = 0; i < data.length; i++) {
        let post = data[i].first;
        let path = data[i].second;
        let card = $("<div class=\"card w-100\"></div>");
        let body = $("<div class=\"card-body\"></div>");
        let img = $("<img src=\"" + path[0] + "\" class=\"card-img-top\" alt=\"...\">");
        let title = $("<h5 class=\"card-title\">" + post.postName + "</h5>");
        let description = $("<p class=\"card-text text-truncate\">" + post.postDescription + "</p>");
        let link = $("<button data-bs-dismiss=\"modal\" onclick=\"displayPost('" + post.postId + "')\" class=\"btn btn-primary stretched-link\">Show Post</button>");
        let space = $("<hr>");
        box.append(card);
        card.append(img);
        card.append(body);
        body.append(title);
        body.append(description);
        body.append(link);
        if (i !== data.length - 1)
            box.append(space);
    }
}

function showSavedPosts(data) {
    let box = $('#savedPostsBox');

    for (let i = 0; i < data.length; i++) {
        let post = data[i].first;
        let path = data[i].second;
        let card = $("<div class=\"card w-100\"></div>");
        let body = $("<div class=\"card-body\"></div>");
        let img = $("<img src=\"" + path[0] + "\" class=\"card-img-top\" alt=\"...\">");
        let title = $("<h5 class=\"card-title\">" + post.postName + "</h5>");
        let description = $("<p class=\"card-text text-truncate\">" + post.postDescription + "</p>");
        let link = $("<button data-bs-dismiss=\"modal\" onclick=\"displayPost('" + post.postId + "')\" class=\"btn btn-primary stretched-link\">Show Post</button>");
        let space = $("<hr>");
        box.append(card);
        card.append(img);
        card.append(body);
        body.append(title);
        body.append(description);
        body.append(link);
        if (i !== data.length - 1)
            box.append(space);
    }
}

function displayPost(postId) {
    $.ajax({
        url: "/posts/view?id=" + postId,
        type: 'GET',
        success: function (data) {
            let post = data.first;
            let paths = data.second;
            let userId = post.userId;

            $.ajax({
                url: "/user_information?id=" + userId,
                type: 'GET',
                success: function (userInformation) {
                    let email = userInformation.email;
                    let firstName = userInformation.firstName;
                    let lastName = userInformation.lastName;
                    let phone = userInformation.phoneNumber;
                    if (firstName == null || lastName == null) {
                        $("#contact-name").addClass("d-none");
                    } else {
                        $("#contact-name").text(firstName + " " + lastName);
                        $("#contact-name").removeClass("d-none");
                    }
                    $("#contact-email").text("E-mail: " + email);
                    if (phone == null) {
                        $("#contact-phone").addClass("d-none");
                    } else {
                        $("#contact-phone").text("Phone: " + phone);
                        $("#contact-phone").removeClass("d-none");
                    }
                    $("#displayed-post-seller-name").text("By: " + userInformation.displayName);
                    $("#displayed-post-country").text("Ships from: " + userInformation.city + ", " + userInformation.country);
                },
                error: function (xhr, status, error) {
                    showStage("home");
                    if (xhr.status && xhr.status === 400)
                        alert(xhr.responseText);
                    else
                        alert("Something went wrong!");
                }
            });
            $("#displayed-post-image").attr("src", paths[0]);
            $("#displayed-post-name").text(post.postName);
            $("#displayed-post-description").text(post.postDescription);
            $("#displayed-post-price").text(post.postPrice.toFixed(2) + "$");
            if (user.id === post.userId || user.marketplaceUserRole === "ADMIN") {
                $("#contact-btn").addClass("d-none");
                $("#watchlist-btn").addClass("d-none");
                $("#delete-post-btn").removeClass("d-none");
                $("#delete-post-btn").click(function () {
                    let deletionData = {
                        "postId": post.postId
                    };
                    $.ajax({
                        url: "posts/delete-post",
                        type: 'POST',
                        data: deletionData,
                        success: function () {
                            $(this).addClass("d-none");
                            alert("Post deleted!");
                            showStage("home");
                        }, error: function (xhr, status, error) {
                            if (xhr.status && xhr.status === 400)
                                alert(xhr.responseText);
                            else
                                alert("Something went wrong!");
                        }
                    });
                });
            } else {
                $("#contact-btn").removeClass("d-none");
                $("#watchlist-btn").removeClass("d-none");
                $("#delete-post-btn").addClass("d-none");
            }

            if (post.userId !== user.id && user.marketplaceUserRole === "ADMIN") {
                $("#ban-user-btn").removeClass("d-none");
                $("#ban-user-btn").click(function () {
                    $.ajax({
                        url: "/ban?id=" + post.userId,
                        type: 'GET',
                        success: function () {
                            alert("User banned!");
                            showStage("home");
                        }, error: function (xhr, status, error) {
                            if (xhr.status && xhr.status === 400)
                                alert(xhr.responseText);
                            else
                                alert("Something went wrong!");
                        }
                    });
                });
            } else
                $("#ban-user-btn").addClass("d-none");

            if ($.post("/posts/is-post-saved", {postId}))
                $("#watchlist-btn").addClass("d-none");
            else
                $("#watchlist-btn").removeClass("d-none");
            $("#watchlist-btn").click(function () {
                $.ajax({
                    url: "/posts/save-post",
                    type: 'POST',
                    data: postId,
                    success: function () {
                        alert("Post saved!");
                        $(this).addClass("d-none");
                    }, error: function (xhr, status, error) {
                        if (xhr.status && xhr.status === 400)
                            alert(xhr.responseText);
                        else
                            alert("Something went wrong!");
                    }
                });
            });
            showStage("displayed-post");
        },
        error: function (xhr, status, error) {
            showStage("home");
            if (xhr.status && xhr.status === 400)
                alert(xhr.responseText);
            else
                alert("Something went wrong!");
        }
    });
}

function showStage(chosenStage) {
    stages.forEach(stage => $('#' + stage).addClass('d-none'));
    $("#" + chosenStage).removeClass('d-none');
}

$(function () {
    $("#newPostForm").on("submit", function (e) {
        e.preventDefault();
        let data = new FormData($('#newPostForm')[0]);
        if (data.get('postDescription') === '')
            data.set('postDescription', ' ');
        $.ajax({
            url: $(this).attr("action"),
            type: 'POST',
            processData: false,
            contentType: false,
            cache: false,
            data: data,
            success: function () {
                showPostBox('myPostsBox');
                showPostSuccess("Posting successful!")
                $('#post-name').val('');
                $('#post-price').val('');
                $('#category').val('NOTHING_SELECTED');
                $('#post-description').val('');
                $('#post-images').val('');
            }, error: function (xhr, status, error) {
                if (xhr.status && xhr.status === 400)
                    showPostError(xhr.responseText);
                else
                    showPostError("Something went wrong!");
            }
        });
    });
});

$(function () {
    let fileInput = $('#post-images');
    fileInput.on("change", function () {
        if (fileInput[0].files.length > 8) {
            showPostError("You cannot upload more than 8 images!");
            fileInput.val('');
        }
    });
});
