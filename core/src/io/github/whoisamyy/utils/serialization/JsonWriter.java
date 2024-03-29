package io.github.whoisamyy.utils.serialization;

import io.github.whoisamyy.logging.LogLevel;
import io.github.whoisamyy.utils.serialization.serializers.Serializer;

public class JsonWriter extends Serializer<JsonObject> {
    StringBuilder jsonBuilder;

    int writtenObjectsCount = 0;

    public JsonWriter(StringBuilder jsonBuilder) {
        super();
        this.jsonBuilder = jsonBuilder;
        startWrite();
        setLogLevel(LogLevel.DEBUG);
    }

    void startWrite() {
        jsonBuilder.append("{");
    }

    void endWrite() {
        jsonBuilder.append("}");
    }

    /**
     * This implementation writes {@link JsonObject#root} and returns written String.
     * @param object Object to be written. May be null because this method writes {@link JsonObject#root}
     * @return Json-String representation of objects in root's {@link JsonObject#children}
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public String writeObject(JsonObject object) throws IllegalAccessException {
        StringBuilder localSb = new StringBuilder();
        localSb.append("{");
        startWrite();
        debug(object.getValue().toString());

        //Field[] fields = object.getValue().getClass().getDeclaredFields();

        for (JsonObject child : JsonObject.getRoot().children) {
            if (child.getType()==null) // if child is empty/null
                break;

            if (child.getValue().getClass().isPrimitive()) { // if type is primitive
                debug("Child's type is primitive");

                jsonBuilder.append('"').append(child.getName()).append('"').append(":").append(child.getValue());
                localSb.append('"').append(child.getName()).append('"').append(":").append(child.getValue());
            } else if (child.getValue().getClass().equals(String.class)) { // if type is String
                debug("Child's type is String");

                jsonBuilder.append('"').append(child.getName()).append("\":\"").append(child.getValue()).append("\"");
                localSb.append('"').append(child.getName()).append("\":\"").append(child.getValue()).append("\"");
            } else if (Json.knowsExtender(child.getValue().getClass())) { // if type is known
                debug("Child's type is known: " + child.getType());

                String wo;
                wo = Json.getByExtender(child.getType()).writeObject(child.getValue());

                jsonBuilder.append('"').append(child.getName()).append('"').append(":").append(wo);
                localSb.append('"').append(child.getName()).append('"').append(":").append(wo);
            } else { // if type is unknown
                debug("Child's type is unknown: " + child.getType());

                jsonBuilder.append('"').append(child.getName()).append('"').append(":").append('"').append(child.getValue()).append('"');
                localSb.append('"').append(child.getName()).append('"').append(":").append('"').append(child.getValue()).append('"');
            }
            localSb.append(",");
        }

        /* this code will remain here. reason is: придумтьа причину
        for (Field f : fields) {
            boolean isAccessible;

            if (Modifier.isStatic(f.getModifiers())) {
                isAccessible = f.canAccess(null);
            }
            else {
                isAccessible = f.canAccess(object.getValue());
            }

            f.setAccessible(true);

            if (f.getClass().isPrimitive()) { // if type is primitive
                jsonBuilder.append(f.getName()).append(":").append(f.get(object.getValue()));
                localSb.append(f.getName()).append(":").append(f.get(object.getValue()));
            } else if (f.getClass().equals(String.class)) { // if type is String
                jsonBuilder.append(f.getName()).append(":\"").append(f.get(object.getValue())).append("\"");
                localSb.append(f.getName()).append(":\"").append(f.get(object.getValue())).append("\"");
            } else if (Json.knownTypes.containsKey(f.getClass())) { // if type is known
                String wo;
                wo = Json.knownTypes.get(f.getClass()).writeObject(f.get(object.getValue()));

                jsonBuilder.append(f.getName()).append(":").append(wo);
                localSb.append(f.getName()).append(":").append(wo);
            } else { // if type is unknown
                jsonBuilder.append(f.getName()).append(":").append('"').append(f.get(object.getValue())).append('"');
                localSb.append(f.getName()).append(":").append('"').append(f.get(object.getValue())).append('"');
            }

            f.setAccessible(isAccessible);

            jsonBuilder.append(",");
            localSb.append(",");
        }
         */

        try {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
            localSb.deleteCharAt(localSb.length() - 1);
        } catch (IndexOutOfBoundsException ignored) {}

        endWrite();
        localSb.append("}");

        if (writtenObjectsCount>0) {
            jsonBuilder.append(",");
        }

        debug(localSb.toString());

        return localSb.toString();
    }
}
