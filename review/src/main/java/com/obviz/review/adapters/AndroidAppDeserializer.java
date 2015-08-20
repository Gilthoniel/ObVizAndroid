package com.obviz.review.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.OpinionValue;

import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * Created by gaylor on 08/19/2015.
 * Parse from json source and remove some useless element before send it
 */
public class AndroidAppDeserializer implements JsonDeserializer<AndroidApp> {
    @Override
    public AndroidApp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        AndroidApp app = AndroidApp.fromJson(jsonElement.getAsJsonObject());

        Iterator<OpinionValue> it = app.getOpinions().iterator();
        while (it.hasNext()) {
            if (!it.next().isValid()) {
                it.remove();
            }
        }

        return app;
    }
}
