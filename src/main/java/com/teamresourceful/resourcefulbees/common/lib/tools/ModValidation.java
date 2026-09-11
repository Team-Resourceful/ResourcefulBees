package com.teamresourceful.resourcefulbees.common.lib.tools;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;

public final class ModValidation {

    public static boolean IS_RUNNING_IN_IDE = false;

    private ModValidation() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void init() {
        if (IS_RUNNING_IN_IDE) {
            ResourcefulBeesAPI.getInitializers().validate();
            ResourcefulBeesAPI.getHoneyInitializers().validate();
        }
    }
}
