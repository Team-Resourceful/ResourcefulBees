package com.teamresourceful.resourcefulbees.common.lib.data.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

import java.util.function.Predicate;

public interface Conditional extends Predicate<JsonObject> {

    Identifier getId();
}
