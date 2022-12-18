package com.teamresourceful.resourcefulbees.platform.common.item.forge;

import com.teamresourceful.resourcefulbees.platform.common.item.ItemAction;

public class ItemActionHelperImpl {
    public static ItemAction create(String id) {
        return new ForgeItemAction(id);
    }
}
