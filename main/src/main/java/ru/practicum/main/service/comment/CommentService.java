package ru.practicum.main.service.comment;

import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.CreateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long eventId, CreateCommentDto commentDto, Long userId);

    List<CommentDto> getCommentsForEvent(Long eventId);

    List<CommentDto> getCommentsByUser(Long userId);

    CommentDto update(Long eventId, Long commentId, CreateCommentDto commentDto, Long userId);

    void delete(Long userId, Long id);

    void deleteAdmin(Long id);

    CommentDto getComment(Long commentId);
}
