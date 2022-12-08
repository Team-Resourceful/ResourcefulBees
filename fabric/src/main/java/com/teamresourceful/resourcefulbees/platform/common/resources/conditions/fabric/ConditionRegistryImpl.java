package com.teamresourceful.resourcefulbees.platform.common.resources.conditions.fabric;

import com.teamresourceful.resourcefulbees.platform.common.resources.conditions.Conditional;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

public class ConditionRegistryImpl {
    public static void registerCondition(Conditional condition) {
        ResourceConditions.register(condition.getId(), condition);
    }
}
