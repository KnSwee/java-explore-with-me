package ru.practicum.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.service.comment.CommentService;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteCommentAdmin(@PathVariable(name = "commentId") Long commentId) {
        commentService.deleteAdmin(commentId);
    }

}
