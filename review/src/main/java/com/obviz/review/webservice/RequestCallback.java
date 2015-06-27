package com.obviz.review.webservice;

import java.lang.reflect.Type;

/**
 * Created by gaylor on 26.06.15.
 * Action to perform when a request is over
 */
public interface RequestCallback<T> {
    /**
     * Call when the request execute the result
     * @param result Instance of the result
     */
    void execute(T result);

    /**
     * Return the type of the result
     * @return Type reflection
     */
    Type getType();
}
