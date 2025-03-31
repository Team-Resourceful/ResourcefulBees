package com.teamresourceful.resourcefulbees.platform.common.resources.conditions;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class ConditionRegistry {

    @ExpectPlatform
    public static void registerCondition(Conditional condition) {
        throw new NotImplementedException();
    }
}
