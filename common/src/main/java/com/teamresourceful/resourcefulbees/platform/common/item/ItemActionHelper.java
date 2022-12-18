package com.teamresourceful.resourcefulbees.platform.common.item;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemActionHelper {

    private static final Map<String, ItemAction> ACTIONS = new ConcurrentHashMap<>();

    private ItemActionHelper() {
        throw new UtilityClassError();
    }

    public static ItemAction get(String id) {
        return ACTIONS.computeIfAbsent(id, ItemActionHelper::create);
    }

    @ExpectPlatform
    private static ItemAction create(String id) {
        throw new NotImplementedException();
    }
}
