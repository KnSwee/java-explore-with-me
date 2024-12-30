package ru.practicum.main.service.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.model.Comment;
import ru.practicum.main.service.user.mapper.UserDtoMapper;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.main.util.Constant.TIME_FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDtoMapper {

    public static CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthor(UserDtoMapper.toUserDto(comment.getAuthor()));
        dto.setEventId(comment.getEvent().getId());
        dto.setCreated(comment.getCreated().format(TIME_FORMATTER));
        return dto;
    }

    public static List<CommentDto> toDto(List<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(toDto(comment));
        }
        return dtos;
    }

}
