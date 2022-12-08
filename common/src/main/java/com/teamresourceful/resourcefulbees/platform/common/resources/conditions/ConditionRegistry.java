package com.teamresourceful.resourcefulbees.platform.common.resources.conditions;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.lang3.NotImplementedException;

public class ConditionRegistry {

    @ExpectPlatform
    public static void registerCondition(Conditional condition) {
        throw new NotImplementedException("Not implemented on this platform");
    }
}
