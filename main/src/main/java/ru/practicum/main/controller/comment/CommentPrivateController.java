package ru.practicum.main.controller.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.CreateCommentDto;
import ru.practicum.main.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/events/{eventId}/comments")
    public CommentDto createComment(@PathVariable(name = "eventId") Long eventId,
                                    @Valid @RequestBody CreateCommentDto commentDto,
                                    @RequestHeader("X-User-Id") Long userId) {
        return commentService.create(eventId, commentDto, userId);
    }

    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getComments(@PathVariable(name = "userId") Long userId) {
        return commentService.getCommentsByUser(userId);
    }

    @GetMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto getComment(@PathVariable(name = "commentId") Long commentId) {
        return commentService.getComment(commentId);
    }

    @PatchMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable(name = "eventId") Long eventId,
                                    @PathVariable(name = "commentId") Long commentId,
                                    @Valid @RequestBody CreateCommentDto commentDto,
                                    @RequestHeader("X-User-Id") Long userId) {
        return commentService.update(eventId, commentId, commentDto, userId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    public void deleteComment(@PathVariable(name = "commentId") Long commentId,
                              @RequestHeader("X-User-Id") Long userId) {
        commentService.delete(userId, commentId);
    }

}
