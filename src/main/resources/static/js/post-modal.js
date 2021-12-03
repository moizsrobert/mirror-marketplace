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
        let link = $("<button data-bs-dismiss=\"modal\" onclick=\"displayPost('" + post.postId + "')\" class=\"btn btn-primary\">Show Post</button>");
        let remove = $("<button onclick=\"removeFromSaved('" + post.postId + "')\" class=\"btn btn-danger mx-1\">Remove</button>");
        let space = $("<hr>");
        box.append(card);
        card.append(img);
        card.append(body);
        body.append(title);
        body.append(description);
        body.append(link);
        body.append(remove);
        if (i !== data.length - 1)
            box.append(space);
    }
}

function removeFromSaved(postId) {
    let removeData = {
        "postId" : postId
    };
    $.ajax({
        url: "/posts/remove-saved-post",
        type: 'POST',
        data: removeData,
        success: function () {
            showPostBox('savedPostsBox');
        }, error: function (xhr, status, error) {
            if (xhr.status && xhr.status === 400)
                alert(xhr.responseText);
            else
                alert("Something went wrong!");
        }
    });
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