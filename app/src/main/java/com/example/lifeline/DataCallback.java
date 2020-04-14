package com.example.lifeline;

public interface DataCallback<T> {
    void onData(DataWrapper<T> wrapper);

}