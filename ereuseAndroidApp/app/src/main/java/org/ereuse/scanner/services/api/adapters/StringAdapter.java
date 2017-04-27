package org.ereuse.scanner.services.api.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;

import java.lang.reflect.Type;

/**
 * Created by martin on 4/04/17.
 */

public class StringAdapter implements JsonSerializer<String> {

    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.equals("")) {
            return (JsonElement) JsonNull.INSTANCE;
        }

        return new JsonPrimitive(src.toString());
    }

}

