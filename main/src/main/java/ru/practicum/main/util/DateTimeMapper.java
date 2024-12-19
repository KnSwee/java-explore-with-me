package ru.practicum.main.util;

import java.time.LocalDateTime;
import java.util.Optional;

import static ru.practicum.main.util.Constant.TIME_FORMATTER;

public class DateTimeMapper {

    public static String getStringFromDate(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime).map(date -> date.format(TIME_FORMATTER)).orElse(null);
    }

    public static LocalDateTime getDateTimeFromString(String dateString) {
        return Optional.ofNullable(dateString).map(date -> LocalDateTime.parse(date, TIME_FORMATTER)).orElse(null);
    }

}
