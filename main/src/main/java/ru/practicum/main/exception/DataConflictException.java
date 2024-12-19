package ru.practicum.main.exception;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String s) {
        super(s);
    }
}
