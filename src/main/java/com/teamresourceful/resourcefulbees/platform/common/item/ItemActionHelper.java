package com.teamresourceful.resourcefulbees.platform.common.item;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemActionHelper {

    private static final Map<String, ItemAction> ACTIONS = new ConcurrentHashMap<>();

    private ItemActionHelper() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static ItemAction get(String id) {
        return ACTIONS.computeIfAbsent(id, ItemActionHelper::create);
    }

    @ExpectPlatform
    private static ItemAction create(String id) {
        throw new NotImplementedException();
    }
}
