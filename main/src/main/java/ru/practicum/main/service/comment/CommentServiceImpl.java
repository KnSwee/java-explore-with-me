package ru.practicum.main.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.CreateCommentDto;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ElementNotFoundException;
import ru.practicum.main.exception.ForbiddenException;
import ru.practicum.main.model.Comment;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.CommentRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.comment.mapper.CommentDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Override
    public CommentDto create(Long eventId, CreateCommentDto commentDto, Long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ElementNotFoundException("Event with id " + eventId + " not found"));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ElementNotFoundException("User with id " + userId + " not found"));

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        Comment saved = commentRepository.save(comment);
        return CommentDtoMapper.toDto(saved);
    }

    @Override
    public List<CommentDto> getCommentsForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ElementNotFoundException("Event with id " + eventId + " not found");
        }
        return CommentDtoMapper.toDto(commentRepository.findByEventId(eventId));
    }

    @Override
    public List<CommentDto> getCommentsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("User with id " + userId + " not found");
        }
        return CommentDtoMapper.toDto(commentRepository.findByAuthorId(userId));
    }

    @Override
    public CommentDto update(Long eventId, Long commentId, CreateCommentDto commentDto, Long userId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ElementNotFoundException("Event with id " + eventId + " not found");
        }
        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("User with id " + userId + " not found");
        }
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ElementNotFoundException("Comment with id " + commentId + " not found")
        );
        Long commentEventId = comment.getEvent().getId();
        Long commentUserId = comment.getAuthor().getId();
        if (!Objects.equals(commentEventId, eventId)) {
            throw new BadRequestException("Неправильно составлен запрос: комментарий написан к событию id " +
                    commentEventId + ", а передан id события eventId = " + eventId);
        }
        if (!Objects.equals(commentUserId, userId)) {
            throw new BadRequestException("Неправильно составлен запрос: комментарий написан пользователем с id " +
                    commentUserId + ", а передан id пользователя userId = " + userId);
        }
        if (comment.getCreated().isBefore(LocalDateTime.now().minusHours(2))) {
            throw new ForbiddenException("Нельзя редактировать комментарий по прошествии двух часов с момента его создания");
        }
        comment.setText(commentDto.getText());
        comment.setUpdated(LocalDateTime.now());
        Comment saved = commentRepository.save(comment);
        return CommentDtoMapper.toDto(saved);
    }

    @Override
    public void delete(Long userId, Long id) {
        if (!userRepository.existsById(userId)) {
            throw new ElementNotFoundException("User with id " + userId + " not found");
        }
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new ElementNotFoundException("Comment with id " + id + " not found")
        );
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new ForbiddenException("Недостаточно прав. Пользователь с id " + userId + " не является автором комментария с id = " + id);
        }
        commentRepository.delete(comment);
    }

    @Override
    public void deleteAdmin(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ElementNotFoundException("Comment with id " + id + " not found");
        }
        commentRepository.deleteById(id);
    }

    @Override
    public CommentDto getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ElementNotFoundException("Comment with id " + commentId + " not found"));
        return CommentDtoMapper.toDto(comment);
    }


}
