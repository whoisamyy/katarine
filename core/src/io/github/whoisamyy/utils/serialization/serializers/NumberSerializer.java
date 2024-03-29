package io.github.whoisamyy.utils.serialization.serializers;

import io.github.whoisamyy.utils.serialization.JsonObject;

public class NumberSerializer<T extends Number> extends Serializer<T> {
    @Override
    public String writeObject(T object) throws IllegalAccessException {
        debug(String.valueOf(object));
        return String.valueOf(object);
    }

    @Override
    public JsonObject toJsonObject(T object) throws IllegalAccessException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.setValue(object);
        jsonObject.setType(object.getClass());
        jsonObject.setName(jsonObject.getType().getName());

        debug(jsonObject.getName()+" "+jsonObject.getType()+" "+jsonObject.getValue());

        return jsonObject;
    }
}
