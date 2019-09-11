/*回复*/
function comment2target(commentId,type,content) {
    if (!content){
        alert("不能回复空内容~~");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": commentId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    var isAccepetd = confirm(response.message);
                    if (isAccepetd) {
                        window.open("https://github.com/login/oauth/authorize?client_id=2a8dd961c09bd53f765a&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", "true");
                    }
                } else {
                    alert(response.message)
                }
            }
        },
        dataType: "json"
    })
}


/**
 * 提交问题回复（一级评论）
*/
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();

    comment2target(questionId,1,content);
}

/**
 * 提交评论回复（二级评论）
 */
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-"+commentId).val();
    comment2target(commentId,2,content);
}

function deletequstion(e) {
    var questionId = e.getAttribute("data-id");
    console.log(questionId);
    var isAccepetd = confirm("是否删除该提问？");
    if (isAccepetd) {
        window.location.href = ("http://106.75.30.28/publish/delete/" + questionId);
    }

}



/**
 *展开二级评论
 */
function collapseComments(e) {
    //一级回复的id
    var id = e.getAttribute("data-id");
    var comments = $("#comment-"+ id);
    //获取二级评论的展开状态，如果collapse存在则表示二级评论已展开
    var collapse = e.getAttribute("data-collapse");
    if (collapse){
        //折叠二级评论（移除二级评论的class "in" 以及 移除该标签（使用该方法的标签）中的展开状态信息："data-collapse"）
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        //移除该标签中的active class
        e.classList.remove("active");
    } else{

        var subCommentContainer = $("#comment-"+id);
        if (subCommentContainer.children().length != 1){
            //展开二级评论(给二级评论的class 增加in)
            comments.addClass("in");
            //在该标签（使用该方法的标签）增加一个属性:"data-collapse"（展开信息），值为"in"
            e.setAttribute("data-collapse","in");
            //在该标签中的class增加active
            e.classList.add("active");
        } else{
            //先获取二级评论，再展开
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });

                //展开二级评论(给二级评论的class 增加in)
                comments.addClass("in");
                //在该标签（使用该方法的标签）增加一个属性:"data-collapse"（展开信息），值为"in"
                e.setAttribute("data-collapse","in");
                //在该标签中的class增加active
                e.classList.add("active");
            });
        }



    }

}

