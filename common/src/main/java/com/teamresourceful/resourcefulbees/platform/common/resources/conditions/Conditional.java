package com.teamresourceful.resourcefulbees.platform.common.resources.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public interface Conditional extends Predicate<JsonObject> {

    ResourceLocation getId();
}
