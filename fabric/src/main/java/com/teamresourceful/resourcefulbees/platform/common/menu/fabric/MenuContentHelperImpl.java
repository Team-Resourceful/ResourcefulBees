package com.teamresourceful.resourcefulbees.platform.common.menu.fabric;

import com.teamresourceful.resourcefulbees.platform.common.menu.ContentMenuProvider;
import com.teamresourceful.resourcefulbees.platform.common.menu.MenuContentHelper;
import com.teamresourceful.resourcefulbees.platform.common.menu.MenuContent;
import com.teamresourceful.resourcefulbees.platform.common.menu.MenuContentSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class MenuContentHelperImpl {
    public static <T extends AbstractContainerMenu, C extends MenuContent<C>> MenuType<T> create(MenuContentHelper.MenuFactory<T, C> factory, MenuContentSerializer<C> serializer) {
        return null;
    }

    public static <C extends MenuContent<C>> void open(ServerPlayer player, ContentMenuProvider<C> provider) {
    }
}
