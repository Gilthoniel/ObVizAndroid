package com.obviz.review;

import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.review.webservice.tasks.HttpTask;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WebServiceTest {

    Set<HttpTask<?>> stacker = new HashSet<>();

    @Test
    public void testRequestsSet() {

        ConnectionService.instance.setRequestsSet(stacker);

        fakeRequest(0);
    }

    private void fakeRequest(final int counter) {
        HashMap<String, String> params = new HashMap<>();

        ConnectionService.instance.execute("http://www.google.com", params, new RequestCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                manageCounter(counter);
            }

            @Override
            public void onFailure(Errors error) {
                manageCounter(counter);
            }

            @Override
            public Type getType() {
                return Void.class;
            }
        }, null, false);
    }

    private void manageCounter(int counter) {
        if (counter < 10) {
            fakeRequest(counter + 1);
        } else {

            Assert.assertEquals("Test that the stacker is empty after all requests are done",
                    0, stacker.size());
        }
    }
}