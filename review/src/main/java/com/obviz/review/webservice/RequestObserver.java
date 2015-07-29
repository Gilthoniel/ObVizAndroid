package com.obviz.review.webservice;

/**
 * Created by gaylor on 23.07.15.
 *
 */
public interface RequestObserver<T> {
    void onSuccess(T object);
}
