package com.teamresourceful.resourcefulbees.common.lib.tools;

import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;

public final class ModValidation {

    public static boolean IS_RUNNING_IN_IDE = false;

    private ModValidation() {
        throw new UtilityClassError();
    }

    public static void init() {
        if (IS_RUNNING_IN_IDE) {
            NBTConstants.validate();
            ResourcefulBeesAPI.getInitializers().validate();
            ResourcefulBeesAPI.getHoneyInitalizers().validate();
        }
    }
}
