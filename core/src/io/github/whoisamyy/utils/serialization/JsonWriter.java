package io.github.whoisamyy.utils.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;

public class JsonWriter extends Serializer<JsonObject> {
    StringBuilder jsonBuilder;

    int writtenObjectsCount = 0;

    public JsonWriter(StringBuilder jsonBuilder) {
        super(JsonObject.class);
        this.jsonBuilder = jsonBuilder;
        startWrite();
    }

    void startWrite() {
        jsonBuilder.append("{");
    }

    void endWrite() {
        jsonBuilder.append("}");
    }

    public String writeObject(JsonObject object) throws IllegalAccessException {
        StringBuilder localSb = new StringBuilder();
        localSb.append("{");
        startWrite();
        Field[] fields = object.getValue().getClass().getDeclaredFields();

        for (Field f : fields) {
            boolean isAccessible;

            if (Modifier.isStatic(f.getModifiers())) {
                isAccessible = f.canAccess(null);
            }
            else {
                isAccessible = f.canAccess(object.getValue());
            }

            f.setAccessible(true);

            if (f.getType().isPrimitive()) {
                jsonBuilder.append(f.getName()).append(":").append(f.get(object.getValue()));
                localSb.append(f.getName()).append(":").append(f.get(object.getValue()));
            } else if (f.getType().equals(String.class)) {
                jsonBuilder.append(f.getName()).append(":\"").append(f.get(object.getValue())).append("\"");
                localSb.append(f.getName()).append(":\"").append(f.get(object.getValue())).append("\"");
            } else if (Json.knownTypes.containsKey(f.getType())) {
                String wo;
                if (isAccessible) {
                    wo = Json.knownTypes.get(f.getType()).writeObject(JsonObject.toJsonObject(f.get(object.getValue())));
                } else {
                    wo = writeObject(JsonObject.toJsonObject(f.get(object.getValue())));
                }
                jsonBuilder.append(f.getName()).append(":").append(wo);
                localSb.append(f.getName()).append(":").append(wo);
            } else {
                jsonBuilder.append(f.getName()).append(":").append(f.get(object.getValue()));
                localSb.append(f.getName()).append(":").append(f.get(object.getValue()));
            }

            f.setAccessible(isAccessible);

            jsonBuilder.append(",");
            localSb.append(",");
        }
        try {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
            localSb.deleteCharAt(localSb.length() - 1);
        } catch (IndexOutOfBoundsException ignored) {}

        endWrite();
        localSb.append("}");

        if (writtenObjectsCount>0) {
            jsonBuilder.append(",");
        }

        return localSb.toString();
    }
}
