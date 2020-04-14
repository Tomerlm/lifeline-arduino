package com.example.lifeline;

public class DataWrapper<T> {

    private Exception exception;
    private T data;

    public DataWrapper(Exception exception, T data) {
        this.exception = exception;
        this.data = data;
    }

    public static <T>DataWrapper<T> exception(Exception exception) {
        return new DataWrapper<>(exception, null);
    }

    public static <U>DataWrapper<U> with(U data) {
        return new DataWrapper<>(null, data);
    }

    public boolean success() {
        return exception == null;
    }

    public Exception getException() {
        return exception;
    }

    public T get() {
        return data;
    }
}