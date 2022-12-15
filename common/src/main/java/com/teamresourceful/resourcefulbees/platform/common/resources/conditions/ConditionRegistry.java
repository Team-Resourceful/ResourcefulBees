package com.teamresourceful.resourcefulbees.platform.common.resources.conditions;

import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class ConditionRegistry {

    @ExpectPlatform
    public static void registerCondition(Conditional condition) {
        throw new NotImplementedError();
    }
}
