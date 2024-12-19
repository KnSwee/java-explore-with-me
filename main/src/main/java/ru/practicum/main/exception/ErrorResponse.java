package ru.practicum.main.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static ru.practicum.main.util.Constant.TIME_FORMATTER;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorResponse {
    private String status;
    private String reason; //Причина ошибки (из хендлера)
    private String message; //Сообщение об ошибке из сервиса (или места, где произошла ошибка)
    private String timestamp;

    public ErrorResponse(String status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(TIME_FORMATTER);
    }
}
