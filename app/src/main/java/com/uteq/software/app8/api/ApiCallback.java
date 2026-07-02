package com.uteq.software.app8.api;

public interface ApiCallback<T> {
    void onSuccess(T result);

    void onError(String message);
}
