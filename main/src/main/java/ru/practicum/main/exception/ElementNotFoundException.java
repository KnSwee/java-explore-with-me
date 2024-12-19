package ru.practicum.main.exception;

public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String s) {
        super(s);
    }
}
