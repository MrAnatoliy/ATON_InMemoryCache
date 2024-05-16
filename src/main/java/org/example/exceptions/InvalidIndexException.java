package org.example.exceptions;

public class InvalidIndexException extends RuntimeException{
    public InvalidIndexException() { super(); }
    public InvalidIndexException(String message) { super(message); }
}
