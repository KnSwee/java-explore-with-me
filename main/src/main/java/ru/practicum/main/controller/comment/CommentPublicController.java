package ru.practicum.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/events/{eventId}")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/comments")
    public List<CommentDto> getCommentsForEvent(@PathVariable long eventId) {
        return commentService.getCommentsForEvent(eventId);
    }

}
