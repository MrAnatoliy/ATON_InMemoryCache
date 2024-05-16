package org.example.exceptions;

public class RecordAlreadyExistException extends RuntimeException{
    public RecordAlreadyExistException() { super(); }
    public RecordAlreadyExistException(String message) { super(message); }
}
