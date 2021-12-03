let user = setUser();
let previousStage = 'home';
const urlParams = new URLSearchParams(window.location.search);
const stages = [
    'home',
    'search',
    'displayed-post'
]
const priceFormatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
});

function setUser() {
    $.ajax({
        url: '/profile',
        type: 'GET',
        success: function (data) {
            user = data;
        }
    });
}

if (urlParams.has("post")) {
    const postIdFromUrl = urlParams.get('post');
    displayPost(postIdFromUrl);
} else {
    loadLatest();
    loadRandom();
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
                        $("#contact-phone").text("Phone: +" + phone);
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
            $("#displayed-post-price").text(priceFormatter.format(post.postPrice));

            let carousel = $('#carouselBox');
            carousel.empty();

            if (paths[0] === "/img/no-image.jpg") {
                $("#displayed-post-image").attr("role", "");
                $("#displayed-post-image").attr("data-bs-toggle", "");
                $("#displayed-post-image").attr("data-bs-target", "");
            } else {
                $("#displayed-post-image").attr("role", "button");
                $("#displayed-post-image").attr("data-bs-toggle", "modal");
                $("#displayed-post-image").attr("data-bs-target", "#image-modal");
                for (let i = 0; i < paths.length; i++) {
                    let imageWrapper = $;
                    if (i === 0)
                        imageWrapper = $('<div class="carousel-item active"></div>');
                    else
                        imageWrapper = $('<div class="carousel-item"></div>');
                    let image = $('<img style="height: 650px" src="' + paths[i] + '">');
                    imageWrapper.append(image);
                    carousel.append(imageWrapper);
                }
            }

            $("#copy-link-btn").unbind();
            $("#copy-link-btn").click(function () {
                window.prompt("Copy to clipboard", "localhost:8080/home?post=" + postId);
            });

            $('#recommended-box').empty();
            let category = {"category": post.postCategory}
            $.ajax({
                url: "posts/recommended_fill",
                type: "POST",
                data: category,
                success: function (posts) {
                    if (posts.length === 0)
                        $('#no-recommends').removeClass("d-none");
                    else
                        $('#no-recommends').addClass("d-none");
                    for (let i = 0; i < posts.length; i++) {
                        placePost(posts[i], 'recommended-box');
                    }
                },
                error: function (xhr, status, error) {
                    if (xhr.status && xhr.status === 400)
                        alert(xhr.responseText);
                    else
                        alert("Something went wrong!");
                }
            });

            if (user.id === post.userId || user.marketplaceUserRole === "ADMIN") {
                $("#contact-btn").addClass("d-none");
                $("#watchlist-btn").addClass("d-none");
                $("#delete-post-btn").removeClass("d-none");
                $("#delete-post-btn").unbind();
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
                $("#ban-user-btn").unbind();
                $("#ban-user-btn").click(function () {
                    let banData = {
                        "userId": post.userId
                    };
                    $.ajax({
                        url: "/ban",
                        type: 'POST',
                        data: banData,
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

            let isPostSaved = postSavedCheck();

            if (isPostSaved)
                $("#watchlist-btn").addClass("d-none");

            $("#watchlist-btn").unbind();
            $("#watchlist-btn").click(function () {
                let saveData = {
                    "postId": post.postId
                };
                $.ajax({
                    url: "/posts/save-post",
                    type: 'POST',
                    data: saveData,
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

function postSavedCheck(postId) {
    $.ajax({
        url: "posts/is-post-saved",
        type: "POST",
        data: postId,
        success: function (data) {
            return data;
        }
    });
}

function showStage(chosenStage) {
    stages.forEach(stage => $('#' + stage).addClass('d-none'));
    $("#" + chosenStage).removeClass('d-none');
    if (chosenStage === "home") {
        $('#search-text').val('');
        $('#search-category').val('NOTHING_SELECTED');
        loadLatest();
        loadRandom();
        previousStage = 'home';
    }
    if (chosenStage === 'search') {
        previousStage = 'search';
    }
}

function loadLatest() {
    $.ajax({
        url: "posts/latest",
        type: "GET",
        success: function (postlist) {
            $('#latest-posts').empty();
            for (let i = 0; i < postlist.length; i++)
                placePost(postlist[i], 'latest-posts');
        }, error: function (xhr, status, error) {
            if (xhr.status && xhr.status === 400)
                alert(xhr.responseText);
            else
                alert("Something went wrong!");
        }

    });
}

function loadRandom() {
    $.ajax({
        url: "posts/random_fill",
        type: "GET",
        success: function (postlist) {
            let container = $('#random-posts');
            container.empty();
            for (let i = 0; i < postlist.length; i++)
                placePost(postlist[i], 'random-posts');
            if (postlist.length === 0)
                container.append("<h2 class='text-center'>No posts found!</h2>")
        }, error: function (xhr, status, error) {
            if (xhr.status && xhr.status === 400)
                alert(xhr.responseText);
            else
                alert("Something went wrong!");
        }

    });
}

$('#search-form').on("submit", function (e) {
    e.preventDefault();
    $.ajax({
        url: 'posts/search',
        type: 'POST',
        data: $(this).serialize(),
        success: function (data) {
            showStage('search');
            $('#search-result-count').text(data.length + ' posts found');
            $('#search-posts').empty();
            for (let i = 0; i < data.length; i++)
                placePost(data[i], 'search-posts');
        }, error: function (xhr, status, error) {
            if (xhr.status && xhr.status === 400)
                alert(xhr.responseText);
            else
                alert("Something went wrong!");
        }
    });
});

$('#search-category').on("change", function (e) {
    e.preventDefault();
    $.ajax({
        url: 'posts/search',
        type: 'POST',
        data: $(this).serialize(),
        success: function (data) {
            showStage('search');
            $('#search-result-count').text(data.length + ' posts found');
            $('#search-posts').empty();
            for (let i = 0; i < data.length; i++)
                placePost(data[i], 'search-posts');
        }, error: function (xhr, status, error) {
            if (xhr.status && xhr.status === 400)
                alert(xhr.responseText);
            else
                alert("Something went wrong!");
        }
    });
});

function placePost(post, containerName) {
    let container = $('#' + containerName);

    let cardWrapper = $("<div class=\"col-md-6 col-lg-4\"></div>");
    let card = $("<div class=\"card-box position-relative\"></div>");
    let imageWrapper = $("<div class=\"card-thumbnail\"></div>");
    let image = $("<img src=\"" + post.imagePaths[0] + "\" class=\"card-img-top img-fluid\" alt=\"" + post.title + "\">");
    let title = $("<h3 class=\"mt-1\">" + post.title + "</h3>");
    let description = $("<p class=\"text-secondary\">" + post.description + "</p>");
    let link = $("<a href=\"#\" onclick=\"displayPost('" + post.id + "')\" class=\"stretched-link\"></a>");
    let bottom = $("<div class=\"float-start\"></div>");
    let country = $("<h5 class=\"mb-0\">" + post.country + "</h5>");
    let price = $("<h5 class=\"mb-0\">" + priceFormatter.format(post.price) + "</h5>");

    container.append(cardWrapper);
    cardWrapper.append(card);
    card.append(imageWrapper);
    imageWrapper.append(image);
    card.append(title);
    card.append(description);
    card.append(link);
    card.append(bottom);
    bottom.append(country);
    bottom.append(price);
}

























