package com.resourcefulbees.resourcefulbees.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JSONUtils;

public class BetterJSONUtils {

    public static double getDouble(JsonElement element, String memberName) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsDouble();
        } else {
            throw new JsonSyntaxException("Expected " + memberName + " to be a Double, was " + JSONUtils.toString(element));
        }
    }

    public static double getDouble(JsonObject object, String memberName) {
        if (object.has(memberName)) {
            return getDouble(object.get(memberName), memberName);
        } else {
            throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Double");
        }
    }

    public static double getDouble(JsonObject object, String memberName, double fallback) {
        return object.has(memberName) ? getDouble(object.get(memberName), memberName) : fallback;
    }
}
