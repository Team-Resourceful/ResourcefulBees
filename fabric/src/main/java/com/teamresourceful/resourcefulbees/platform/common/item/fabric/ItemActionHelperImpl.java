package com.teamresourceful.resourcefulbees.platform.common.item.fabric;

import com.teamresourceful.resourcefulbees.platform.common.item.ItemAction;

public class ItemActionHelperImpl {
    public static ItemAction create(String id) {
        return new FabricItemAction(id);
    }
}
