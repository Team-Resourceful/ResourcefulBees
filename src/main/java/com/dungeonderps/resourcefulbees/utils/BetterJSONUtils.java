package com.dungeonderps.resourcefulbees.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JSONUtils;

public class BetterJSONUtils {

    public static double getDouble(JsonElement p_151220_0_, String p_151220_1_) {
        if (p_151220_0_.isJsonPrimitive() && p_151220_0_.getAsJsonPrimitive().isNumber()) {
            return p_151220_0_.getAsDouble();
        } else {
            throw new JsonSyntaxException("Expected " + p_151220_1_ + " to be a Double, was " + JSONUtils.toString(p_151220_0_));
        }
    }

    public static double getDouble(JsonObject p_151217_0_, String p_151217_1_) {
        if (p_151217_0_.has(p_151217_1_)) {
            return getDouble(p_151217_0_.get(p_151217_1_), p_151217_1_);
        } else {
            throw new JsonSyntaxException("Missing " + p_151217_1_ + ", expected to find a Double");
        }
    }

    public static double getDouble(JsonObject p_151221_0_, String p_151221_1_, double p_151221_2_) {
        return p_151221_0_.has(p_151221_1_) ? getDouble(p_151221_0_.get(p_151221_1_), p_151221_1_) : p_151221_2_;
    }
}
